package com.ls.dailytaskplanner.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.database.TaskRepository
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.model.Task
import com.ls.dailytaskplanner.ui.MainActivity
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.AppUtils.calculateTimeRemaining
import com.ls.dailytaskplanner.utils.Constants
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.NotificationUtils
import com.ls.dailytaskplanner.utils.NotificationUtils.NOTIFICATION_ID_REMIND_CREATE_PLAN_TODAY
import com.ls.dailytaskplanner.utils.NotificationUtils.NOTIFICATION_ID_REMIND_TASK
import com.ls.dailytaskplanner.utils.NotificationUtils.NOTIFICATION_ID_SERVICE
import com.ls.dailytaskplanner.utils.TrackingHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ForegroundService : Service() {

    private var isServiceRunning = false

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var localStorage: LocalStorage


    var isUserPresent = false

    private var mediaPlayer: MediaPlayer? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var numberTaskDone = 0
    private var totalTask = 0

    private var jobCheckStatusTask: Job? = null

    private val broadcastReceiver = object : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            Logger.d(TAG, "----> onReceive action: ${intent.action}")
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> {
                    isUserPresent = true
                }

                Intent.ACTION_SCREEN_OFF -> {
                    isUserPresent = false
                    Logger.d(TAG, "----> Screen off")
                }

                Intent.ACTION_USER_PRESENT -> {
                    isUserPresent = true
                    jobCheckStatusTask?.cancel()
                    jobCheckStatusTask = serviceScope.launch(Dispatchers.IO) {
                        delay(localStorage.timeCheckStatusTask * 1000L)
                        handleUserActive()
                    }
                    Logger.d(TAG, "----> Screen unlock")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkStatusTask() {
        serviceScope.launch {
            val listTask = taskRepository.getTasksByDate(AppUtils.getCurrentDate())
            if (listTask.isEmpty()) {
                showNotifyInviteCreatePlan()
            } else {
                listTask.forEach {
                    if (!it.isCompleted && it.isReminder) {
                        showNotifyRemindTask(it)
                    } else {
                        Logger.d(TAG, "---> Task ${it.title} is done")
                    }
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotifyRemindTask(it: Task) = CoroutineScope(Dispatchers.IO).launch {
        val timeRemaining = it.timeStart.calculateTimeRemaining()
        if (timeRemaining in 1..localStorage.remindTaskBefore.toInt() && !it.didReminder) {
            it.didReminder = true
            taskRepository.updateTask(it)
            TrackingHelper.logEvent(AllEvents.NOTIFY_REMIND_TASK + "receive")
            NotificationUtils.showNotificationRemindTask(
                this@ForegroundService,
                it.title + " " + getString(R.string.remind_task_start_after) + " " + timeRemaining + " " + getString(
                    R.string.minutes
                ),
                it.timeStart,
                PendingIntent.getActivity(
                    this@ForegroundService,
                    0,
                    Intent(this@ForegroundService, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra(
                            Constants.IntentKey.TYPE_NOTIFY,
                            NOTIFICATION_ID_REMIND_TASK
                        )
                    },
                    PendingIntent.FLAG_IMMUTABLE
                ),
                NOTIFICATION_ID_REMIND_TASK
            )
            playSoundNotify()
            Logger.d(
                TAG,
                "---> Task ${it.title} is not done time remain = " + it.timeStart.calculateTimeRemaining() + " minutes"
            )
        }
    }

    private fun showNotifyInviteCreatePlan() {
        val lastTimeInviteCreatePlan = localStorage.lastTimeInviteCreatePlan
        if (lastTimeInviteCreatePlan == 0L || System.currentTimeMillis() - lastTimeInviteCreatePlan > 4 * 60 * 60 * 1000) { // 4h
            localStorage.lastTimeInviteCreatePlan = System.currentTimeMillis()
            TrackingHelper.logEvent(AllEvents.NOTIFY_INVITE_CREATE_PLAN + "receive")
            NotificationUtils.showNotification(
                getString(R.string.effective_plan),
                getString(R.string.effective_plan_des),
                PendingIntent.getActivity(
                    this@ForegroundService,
                    0,
                    Intent(this@ForegroundService, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra(
                            Constants.IntentKey.TYPE_NOTIFY,
                            NOTIFICATION_ID_REMIND_CREATE_PLAN_TODAY
                        )
                    },
                    PendingIntent.FLAG_IMMUTABLE
                ),
                NOTIFICATION_ID_REMIND_CREATE_PLAN_TODAY
            )
        }
    }

    private fun playSoundNotify() {
        if (localStorage.enableSoundNotify) {
            mediaPlayer?.start()
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.me_sound)
        TrackingHelper.logEvent(AllEvents.SERVICE_ON_CREATE)
        Logger.d(TAG, "Service created")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        TrackingHelper.logEvent(AllEvents.SERVICE_ON_START_COMMAND)
        Logger.d(TAG, "Service onStartCommand ")
        // Create a notification channel for Android 8.0 and above
        NotificationUtils.createNotificationChannel()
        // Build the notification
        val notification = buildNotification()

        // Start the foreground service
        startForeground(NOTIFICATION_ID_SERVICE, notification)

        registerEventUser()
        scheduleCheckTask()
        // Keep the service alive
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleCheckTask() {
        serviceScope.launch(Dispatchers.IO) {
            if (isServiceRunning) {
                Logger.d(TAG, "----> Return because service is running")
                return@launch
            }
            isServiceRunning = true
            while (true) {
                Logger.d(TAG, "----> schedule CheckTask every minute")
                delay(TIME_CHECK_TASK)
                checkStatusTask()
            }
        }
    }

    private fun handleUserActive() {
        serviceScope.launch(Dispatchers.IO) {
            val listTask = taskRepository.getTasksByDate(AppUtils.getCurrentDate())
            val totalTask = listTask.size
            val numTaskDone = listTask.count { it.isCompleted }
            this@ForegroundService.numberTaskDone = numTaskDone
            this@ForegroundService.totalTask = totalTask
            updateNotifyForegroundService()
        }
    }

    private fun registerEventUser() {
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        TrackingHelper.logEvent(AllEvents.SERVICE_DESTROY)
        serviceScope.cancel()
        isServiceRunning = false
        unregisterReceiver(broadcastReceiver)
        Logger.d(TAG, "Service destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        TrackingHelper.logEvent(AllEvents.SERVICE_REMOVE_TASK)
        Logger.d(TAG, "Service onTaskRemoved")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    fun updateNotifyForegroundService() {
        startForeground(NOTIFICATION_ID_SERVICE, buildNotification())
    }

    @SuppressLint("RemoteViewLayout")
    private fun buildNotification(): Notification {
        val message =
            getString(R.string.congratulation_done_task_to_now, "$numberTaskDone/$totalTask")
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(message)
            .setSmallIcon(R.drawable.pencil)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    companion object {
        const val TAG = "ForegroundServiceTask"
        const val TIME_CHECK_TASK = 60 * 1000L

    }
}