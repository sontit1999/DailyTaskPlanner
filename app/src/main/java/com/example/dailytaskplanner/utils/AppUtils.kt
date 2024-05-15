package com.example.dailytaskplanner.utils

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
}