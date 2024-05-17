package com.example.dailytaskplanner.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.database.TaskRepository
import com.example.dailytaskplanner.database.storage.LocalStorage
import com.example.dailytaskplanner.ui.MainActivity
import com.example.dailytaskplanner.utils.AppUtils
import com.example.dailytaskplanner.utils.Logger
import com.example.dailytaskplanner.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ForegroundService : Service() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var localStorage: LocalStorage

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Logger.d(TAG, "----> onReceive action: ${intent.action}")
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                // The screen has turned on
                // Do something here, such as start an activity
                checkStatusTask()
            }
        }
    }

    private fun checkStatusTask() {
        serviceScope.launch {
            val listTask = taskRepository.getTasksByDate(AppUtils.getCurrentDate())
            listTask.forEach {
                if (!it.isCompleted) {
                    NotificationUtils.showNotification(
                        "Task Reminder",
                        "Task ${it.title} is not done",
                        PendingIntent.getActivity(
                            this@ForegroundService,
                            0,
                            Intent(this@ForegroundService, MainActivity::class.java),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    Logger.d(TAG, "---> Task ${it.title} is not done")
                } else {
                    Logger.d(TAG, "---> Task ${it.title} is done")
                }
            }
            // check all task done
            if (listTask.isNotEmpty() && listTask.all { it.isCompleted }) {
                NotificationUtils.showNotification(
                    getString(R.string.app_name),
                    getString(R.string.congratulation_done_task),
                    PendingIntent.getActivity(
                        this@ForegroundService,
                        0,
                        Intent(this@ForegroundService, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
                Logger.d(TAG, "---> All tasks are done")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Logger.d(TAG, "Service created")
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d(TAG, "Service onStartCommand ")
        // Create a notification channel for Android 8.0 and above
        NotificationUtils.createNotificationChannel()

        // Build the notification
        val notification = buildNotification()

        // Start the foreground service
        startForeground(1, notification)

        registerEventUser()

        // Keep the service alive
        return START_STICKY
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

    }
}