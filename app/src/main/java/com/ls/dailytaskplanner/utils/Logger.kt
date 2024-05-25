package com.ls.dailytaskplanner.utils

import android.util.Log

object Logger {
    const val TAG_COMMON = "DailyTaskPlanner"

    fun d(message: String) {
        Log.d(TAG_COMMON, message)
    }

    fun d(tag: String, message: String) {
        Log.d(TAG_COMMON, "$tag ----> $message")
    }

    fun e(message: String) {
        Log.e(TAG_COMMON, message)
    }

    fun e(tag: String, message: String) {
        Log.e(TAG_COMMON, "$tag ----> $message")
    }
}