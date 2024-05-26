package com.ls.dailytaskplanner.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.TrackingHelper

class BoostReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Start the foreground service here
            TrackingHelper.logEvent(AllEvents.SERVICE_RESTART_AFTER_BOOST)
            val serviceIntent = Intent(context, ForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}