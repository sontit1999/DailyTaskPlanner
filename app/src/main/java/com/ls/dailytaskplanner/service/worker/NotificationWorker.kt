package com.ls.dailytaskplanner.service.worker

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
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.ui.MainActivity
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.NotificationUtils
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var localStorage: LocalStorage

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        // Code to show notification goes here
        if (localStorage.enableNotifyApp) {
            NotificationUtils.showNotification(
                appContext.getString(R.string.app_name),
                appContext.getString(R.string.message_notify_daily_task_remind),
                PendingIntent.getActivity(
                    appContext,
                    0,
                    Intent(appContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
        scheduleNext()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNext() {
        val nextTimeNotify = getNextTimeNotify()
        val notificationWork =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(nextTimeNotify, TimeUnit.SECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .addTag(TAG)
                .addTag(nextTimeNotify.toString())
                .build()

        WorkManager.getInstance(appContext).enqueue(notificationWork)
    }

    companion object {
        const val TAG = "NotificationWorker"

        @RequiresApi(Build.VERSION_CODES.O)
        fun getNextTimeNotify(): Long {
            val now = LocalDateTime.now()
            val ninePM = LocalTime.of(21, 0)
            val nextNotifyTime = if (now.toLocalTime().isAfter(ninePM)) {
                LocalDateTime.of(now.plusDays(1).toLocalDate(), ninePM)
            } else {
                LocalDateTime.of(now.toLocalDate(), ninePM)
            }
            Logger.d(
                TAG,
                "Next notification time: $nextNotifyTime, time delay in seconds: ${
                    Duration.between(
                        now,
                        nextNotifyTime
                    ).seconds
                }"
            )
            return Duration.between(now, nextNotifyTime).seconds
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun scheduleNotification(context: Context) {
            val nextTime = getNextTimeNotify()
            val notificationWork =
                OneTimeWorkRequestBuilder<NotificationWorker>() // Flex interval is set to 15 minutes
                    .setInitialDelay(nextTime, TimeUnit.SECONDS)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED) // Change to NetworkType.UNMETERED for unmetered network
                            .build()
                    )
                    .addTag(TAG)
                    .addTag(nextTime.toString())
                    .build()

            WorkManager.getInstance(context).enqueue(notificationWork)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
            Logger.d(TAG, "Notification cancelled")
        }
    }
}
