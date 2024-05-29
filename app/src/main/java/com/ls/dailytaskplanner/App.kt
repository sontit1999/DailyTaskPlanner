package com.ls.dailytaskplanner

import android.app.Application
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.utils.AppConfig
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
    }

    companion object{
        lateinit var mInstance: App
    }

}