package com.example.dailytaskplanner.utils

import android.content.Intent
import com.example.dailytaskplanner.App
import com.example.dailytaskplanner.service.ForegroundService
import kotlin.math.floor

object AppUtils {

    fun randomColor(): String {
        val chars = "0123456789ABCDEF"
        var color = "#"
        for (i in 1..6) {
            color += chars[floor(Math.random() * 16).toInt()]
        }
        return color
    }

    fun startTaskService() {
        App.mInstance.startService(
            Intent(
                App.mInstance.applicationContext,
                ForegroundService::class.java
            )
        )
    }
}