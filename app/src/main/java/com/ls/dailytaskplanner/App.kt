package com.ls.dailytaskplanner

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.utils.AppConfig
import com.ls.dailytaskplanner.utils.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var storage: LocalStorage

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
            loadNativeAges()
        }
    }

    private fun loadNativeAges() {
        if (!storage.didChooseLanguage) {
            AdManager.loadNativeAge()
        }
    }

    companion object{
        lateinit var mInstance: App
    }

}