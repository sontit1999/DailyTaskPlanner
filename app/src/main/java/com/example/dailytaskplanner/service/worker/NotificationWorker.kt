package com.example.dailytaskplanner.service.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.dailytaskplanner.ui.MainActivity
import com.example.dailytaskplanner.utils.Logger
import com.example.dailytaskplanner.utils.NotificationUtils
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        // Code to show notification goes here
        NotificationUtils.showNotification(
            "Reminder",
            "Don't forget create a new plan for tomorrow!",
            PendingIntent.getActivity(
                appContext,
                0,
                Intent(appContext, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        scheduleNext()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNext() {
        Logger.d(TAG, "Scheduling next notification")
        val currentTime = LocalDateTime.now()
        val nextTime = LocalDateTime.now().plusDays(1)
        val initialDelay = Duration.between(currentTime, nextTime).toMinutes()

        val notificationWork =
            OneTimeWorkRequestBuilder<NotificationWorker>() // Flex interval is set to 15 minutes
                .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED) // Change to NetworkType.UNMETERED for unmetered network
                        .build()
                )
                .addTag(TAG)
                .build()

        WorkManager.getInstance(appContext).enqueue(notificationWork)
        Logger.d(
            TAG, "Notification scheduled at ${
                nextTime.format(
                    DateTimeFormatter.ofPattern(
                        "dd-MM-yyyy HH:mm:ss"
                    )
                )
            }"
        )
    }

    companion object {
        const val TAG = "NotificationWorker"

        @RequiresApi(Build.VERSION_CODES.O)
        fun scheduleNotification(context: Context) {
            val currentTime = LocalDateTime.now()
            val ninePM = LocalDateTime.now().withHour(15).withMinute(37).withSecond(0)
            val initialDelay = Duration.between(currentTime, ninePM).toMinutes()

            val notificationWork =
                OneTimeWorkRequestBuilder<NotificationWorker>() // Flex interval is set to 15 minutes
                    .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED) // Change to NetworkType.UNMETERED for unmetered network
                            .build()
                    )
                    .addTag(TAG)
                    .build()

            WorkManager.getInstance(context).enqueue(notificationWork)
            Logger.d(
                TAG, "Notification scheduled at ${
                    ninePM.format(
                        DateTimeFormatter.ofPattern(
                            "dd-MM-yyyy HH:mm:ss"
                        )
                    )
                }"
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
        }
    }
}

