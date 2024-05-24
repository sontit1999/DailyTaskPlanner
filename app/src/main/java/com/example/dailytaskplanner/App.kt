package com.example.dailytaskplanner

import android.app.Application
import com.example.dailytaskplanner.utils.AppConfig
import com.example.dailytaskplanner.utils.Logger
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        AppConfig.setup(this)
        initMobileAd()
    }

    private fun initMobileAd() {
        MobileAds.initialize(
            this
        ) {
            Logger.d("-----> MobileAds initialized")
        }
    }

    companion object{
        lateinit var mInstance: App
    }

}