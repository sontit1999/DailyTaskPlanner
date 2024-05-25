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
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SaturdayWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var localStorage: LocalStorage

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        // Code to show notification goes here
        if (localStorage.enableNotifyApp) {
            NotificationUtils.showNotification(
                appContext.getString(R.string.app_name),
                appContext.getString(R.string.notify_saturday),
                PendingIntent.getActivity(
                    appContext,
                    0,
                    Intent(appContext, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
        scheduleNext()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNext() {
        val nextTimeNotify = getNextNotifyTime()
        val notificationWork =
            OneTimeWorkRequestBuilder<SaturdayWorker>()
                .setInitialDelay(
                    Duration.between(LocalDateTime.now(), nextTimeNotify).seconds,
                    TimeUnit.SECONDS
                )
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
        fun getNextNotifyTime(
            dayOfWeek: DayOfWeek = DayOfWeek.SATURDAY,
            hour: Int = 9,
            minute: Int = 0,
        ): LocalDateTime {
            val currentTime = LocalDateTime.now()

            // thời điểm gần nhất
            val timeTarget: LocalDateTime =
                if (currentTime.hour > hour || (currentTime.hour == hour && currentTime.minute > minute)) {
                    LocalDate.now().with(TemporalAdjusters.next(dayOfWeek)).atTime(hour, minute)
                } else {
                    LocalDate.now().atTime(hour, minute)
                }
            Logger.d(
                TAG,
                "ScheduleNotify weekly saturday target ${
                    timeTarget.format(
                        DateTimeFormatter.ofPattern(
                            "dd-MM-yyyy HH:mm:ss"
                        )
                    )
                }"
            )
            return timeTarget
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun scheduleNotification(context: Context) {
            val nextTime = getNextNotifyTime()
            val notificationWork =
                OneTimeWorkRequestBuilder<SaturdayWorker>() // Flex interval is set to 15 minutes
                    .setInitialDelay(
                        Duration.between(LocalDateTime.now(), nextTime).seconds,
                        TimeUnit.SECONDS
                    )
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
            Logger.d(TAG, "SaturdayWorker cancelled")
        }
    }
}
