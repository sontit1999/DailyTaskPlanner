package com.ls.dailytaskplanner.utils

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ls.dailytaskplanner.App
import com.ls.dailytaskplanner.service.ForegroundService
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
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
        if (App.mInstance.applicationContext.isServiceRunning<ForegroundService>()) {
            // Foreground service is running
            Logger.d("---> Foreground service is running")
        } else {
            // Foreground service is not running
            App.mInstance.startService(
                Intent(
                    App.mInstance.applicationContext,
                    ForegroundService::class.java
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String): String {
        val inputDate = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return inputDate.format(formatter)
    }

    fun formatLongToDateString(dateInLong: Long): String {
        return try {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(dateInLong)
            simpleDateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    fun Fragment.hiddenKeyboard(){
        val imm = requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    fun String.toTimeString(): String? {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = sdf.parse(this)
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.calculateTimeRemaining(): Int {
        if(this == "00:00") return 0
        val formatter = DateTimeFormatter.ofPattern("H:mm")
        val givenTime = LocalTime.parse(this, formatter)
        val currentTime = LocalTime.now()

        val duration = Duration.between(currentTime, givenTime)

        return (duration.seconds / 60).toInt()
    }

    @Suppress("DEPRECATION") // Deprecated for third party Services.
    inline fun <reified T> Context.isServiceRunning() =
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == T::class.java.name }

    fun readFileTextFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }


    private val notifyPermission = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS
    )

    fun hasPostNotifyPermissions(context: Context) : Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            return true
        }
        return hasPermissions(context, notifyPermission)
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

}