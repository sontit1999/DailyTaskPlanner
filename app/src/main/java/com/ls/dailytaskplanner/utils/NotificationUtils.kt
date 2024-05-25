package com.ls.dailytaskplanner.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
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

        val notificationLayout =
            RemoteViews(context.packageName, R.layout.custom_notification_remind_task)
        notificationLayout.setTextViewText(R.id.notification_title, title)
        notificationLayout.setTextViewText(
            R.id.notification_time,
            content
        )
        notificationLayout.setImageViewIcon(R.id.notification_icon, Icon.createWithResource(context, R.drawable.icon_task))


        val notify = NotificationCompat.Builder(context, CHANNEL_ID)
            //.setContentTitle(title)
            //.setContentText(content)
            .setSmallIcon(R.drawable.icon_task)
            .setAutoCancel(true)
            //.setOngoing(true)
            .setContentIntent(pendingIntent)
            .setCustomContentView(notificationLayout)
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