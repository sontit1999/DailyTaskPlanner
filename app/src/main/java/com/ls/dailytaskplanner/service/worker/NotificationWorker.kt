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
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.Constants
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.NotificationUtils
import com.ls.dailytaskplanner.utils.TrackingHelper
import java.time.LocalDateTime
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var localStorage: LocalStorage

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        TrackingHelper.logEvent(AllEvents.NOTIFY_DAILY + "receive")
        NotificationUtils.showNotification(
            appContext.getString(R.string.title_notify_daily),
            appContext.getString(R.string.message_notify_daily_task_remind),
            PendingIntent.getActivity(
                appContext,
                0,
                Intent(appContext, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra(
                        Constants.IntentKey.TYPE_NOTIFY,
                        NotificationUtils.NOTIFY_DAILY_OFFLINE
                    )
                },
                PendingIntent.FLAG_IMMUTABLE
            ),
            NotificationUtils.NOTIFY_DAILY_OFFLINE
        )
        scheduleNext()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNext() {

        val notificationWork =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(ONE_DAY, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .addTag(TAG)
                .addTag(LocalDateTime.now().toString())
                .build()

        WorkManager.getInstance(appContext).enqueue(notificationWork)
    }

    companion object {
        const val TAG = "NotificationWorker"
        const val ONE_DAY = 24L


        fun scheduleNotification(context: Context) {
            val notificationWork =
                OneTimeWorkRequestBuilder<NotificationWorker>() // Flex interval is set to 15 minutes
                    .setInitialDelay(ONE_DAY, TimeUnit.HOURS)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED) // Change to NetworkType.UNMETERED for unmetered network
                            .build()
                    )
                    .addTag(TAG)
                    .addTag(Date().toString())
                    .build()

            WorkManager.getInstance(context).enqueue(notificationWork)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
            Logger.d(TAG, "Notification cancelled")
        }
    }
}
