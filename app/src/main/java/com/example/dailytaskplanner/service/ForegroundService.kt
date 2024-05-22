package com.example.dailytaskplanner.service

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
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.database.TaskRepository
import com.example.dailytaskplanner.database.storage.LocalStorage
import com.example.dailytaskplanner.ui.MainActivity
import com.example.dailytaskplanner.utils.AppUtils
import com.example.dailytaskplanner.utils.AppUtils.calculateTimeRemaining
import com.example.dailytaskplanner.utils.Logger
import com.example.dailytaskplanner.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private var mediaPlayer: MediaPlayer? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val broadcastReceiver = object : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            Logger.d(TAG, "----> onReceive action: ${intent.action}")
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                // The screen has turned on
                // Do something here, such as start an activity
                // checkStatusTask()
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
                        val timeRemaining = it.timeStart.calculateTimeRemaining()
                        if (timeRemaining in 1..9) {
                            NotificationUtils.showNotification(
                                it.title,
                                "$timeRemaining " + getString(R.string.remind_task_start_after),
                                PendingIntent.getActivity(
                                    this@ForegroundService,
                                    0,
                                    Intent(this@ForegroundService, MainActivity::class.java),
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
                    } else {
                        Logger.d(TAG, "---> Task ${it.title} is done")
                    }
                }
            }

        }
    }

    private fun showNotifyInviteCreatePlan() {
        val lastTimeInviteCreatePlan = localStorage.lastTimeInviteCreatePlan
        if (lastTimeInviteCreatePlan == 0L || System.currentTimeMillis() - lastTimeInviteCreatePlan > 4 * 60 * 60 * 1000) { // 4h
            localStorage.lastTimeInviteCreatePlan = System.currentTimeMillis()
            NotificationUtils.showNotification(
                getString(R.string.effective_plan),
                getString(R.string.effective_plan_des),
                PendingIntent.getActivity(
                    this@ForegroundService,
                    0,
                    Intent(this@ForegroundService, MainActivity::class.java),
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
        Logger.d(TAG, "Service created")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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

    private fun registerEventUser() {
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        isServiceRunning = false
        unregisterReceiver(broadcastReceiver)
        Logger.d(TAG, "Service destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Logger.d(TAG, "Service onTaskRemoved")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("RemoteViewLayout")
    private fun buildNotification(): Notification {
        val notificationLayout = RemoteViews(packageName, R.layout.custom_notification_layout)
        notificationLayout.setTextViewText(R.id.notification_title, "Foreground Service")
        notificationLayout.setTextViewText(
            R.id.notification_body,
            "Service is running in the foreground."
        )

        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Service is running in the foreground.")
            .setSmallIcon(R.drawable.icon_task)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setCustomContentView(notificationLayout)
            .build()
    }

    companion object {

        const val TAG = "ForegroundServiceTask"
        const val NOTIFICATION_ID_REMIND_TASK = 1
        const val NOTIFICATION_ID_SERVICE = 2
        const val NOTIFICATION_ID_REMIND_CREATE_PLAN_TODAY = 3
        const val TIME_CHECK_TASK = 60 * 1000L

    }
}