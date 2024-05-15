package com.example.dailytaskplanner

import android.app.Application
import com.example.dailytaskplanner.utils.AppConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppConfig.setup(this)
    }
}