package com.ls.dailytaskplanner.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ls.dailytaskplanner.App
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.service.worker.NotificationWorker
import com.ls.dailytaskplanner.service.worker.SaturdayWorker

object NotificationUtils {

    const val CHANNEL_ID = "ForegroundServiceChannel"
    const val CHANNEL_NAME = "Foreground Service Channel"
    const val CHANNEL_DESCRIPTION = "Channel for foreground service"
    const val NOTIFY_DAILY_OFFLINE = 6
    const val NOTIFY_SATURDAY = 5
    const val NOTIFICATION_ID_REMIND_TASK = 1
    const val NOTIFICATION_ID_SERVICE = 2
    const val NOTIFICATION_ID_REMIND_CREATE_PLAN_TODAY = 3
    const val NOTIFICATION_ID_UPDATE_STATUS_TASK = 4

    private val notificationManager =
        App.mInstance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(
        title: String,
        content: String,
        pendingIntent: PendingIntent,
        id: Int = System.currentTimeMillis().toInt()
    ) {
        createNotificationChannel()

        // Build the notification
        val notification =  NotificationCompat.Builder(App.mInstance, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.icon_task)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(id, notification)
    }

    fun showNotificationRemindTask(
        context: Context,
        title: String,
        content: String,
        pendingIntent: PendingIntent,
        id: Int = System.currentTimeMillis().toInt()
    ) {
        createNotificationChannel()

        val notify = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.icon_task)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show the notification
        notificationManager.notify(id, notify)
    }

    fun showNotifyNormal(
        context: Context,
        title: String,
        content: String,
        pendingIntent: PendingIntent,
        id: Int = System.currentTimeMillis().toInt()
    ) {
        createNotificationChannel()

        val notify = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.icon_task)
            .setContentIntent(pendingIntent)
            .build()

        // Show the notification
        notificationManager.notify(id, notify)
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = CHANNEL_DESCRIPTION
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleReminderNotification(context: Context) {
        NotificationWorker.scheduleNotification(context)
        SaturdayWorker.scheduleNotification(context)
    }

    fun cancelReminderNotification(context: Context) {
        NotificationWorker.cancel(context)
        SaturdayWorker.cancel(context)
    }
}