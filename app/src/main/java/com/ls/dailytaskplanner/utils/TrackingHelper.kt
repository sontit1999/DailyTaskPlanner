package com.ls.dailytaskplanner.utils

import android.os.Bundle
import com.google.firebase.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.ls.dailytaskplanner.App


object TrackingHelper {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    fun init() {
        try {
            if (mFirebaseAnalytics == null) {
                mFirebaseAnalytics =
                    FirebaseAnalytics.getInstance(App.mInstance.applicationContext)
            }
        } catch (e : Exception) {
            Logger.d("Fail init because: " + e.message)
        }
    }

    fun logEvent(eventName: String) {
        if (mFirebaseAnalytics == null) init()
        val bundle = Bundle()
        bundle.putString("app version", BuildConfig.VERSION_NAME)
        try {
            mFirebaseAnalytics?.logEvent(eventName, bundle)
            Logger.d("Firebase Event: $eventName")
        } catch (e: Exception) {
            Logger.d("Fail log event because: " + e.message)
        }
    }
}