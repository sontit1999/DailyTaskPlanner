package com.example.dailytaskplanner.utils

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dailytaskplanner.App
import com.example.dailytaskplanner.service.ForegroundService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String): String {
        val inputDate = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return inputDate.format(formatter)
    }

}