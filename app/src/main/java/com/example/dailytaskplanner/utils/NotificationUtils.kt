package com.example.dailytaskplanner.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.dailytaskplanner.App
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.service.worker.NotificationWorker

object NotificationUtils {

    const val CHANNEL_ID = "ForegroundServiceChannel"
    const val CHANNEL_NAME = "Foreground Service Channel"
    const val CHANNEL_DESCRIPTION = "Channel for foreground service"

    private val notificationManager =
        App.mInstance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(
        title: String,
        content: String,
        pendingIntent: PendingIntent
    ) {
        createNotificationChannel()

        // Build the notification
        val notification =  NotificationCompat.Builder(App.mInstance, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.icon_task)
            .setContentIntent(pendingIntent)
            .build()

        // Show the notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
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
    }
}