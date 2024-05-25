package com.ls.dailytaskplanner.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.ls.dailytaskplanner.App
import com.ls.dailytaskplanner.model.eventbus.OpenAdEvent
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.RemoteConfig
import com.ls.dailytaskplanner.utils.TrackingHelper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.*

object AppOpenAdManager : Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    var currentActivity: WeakReference<Activity>? = null

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    fun start() {
        App.mInstance.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /** Request an ad. */
    fun loadAd(context: Context) {

        if (!RemoteConfig.commonConfig.supportOpenAds || !RemoteConfig.commonConfig.isActiveAds) return

        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context, RemoteConfig.commonConfig.openAdKey, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    // Called when an app open ad has loaded.
                    Logger.d("Open ad load success")
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_LOAD_SUCCESS)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Called when an app open ad has failed to load.
                    Logger.d("Open ad load fail : " + loadAdError.message)
                    isLoadingAd = false
                    TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_LOAD_FAIL)
                }
            })
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Shows the ad if one isn't already showing. */
    private fun showAdIfAvailable(
        activity: Activity
    ) {

        if (!RemoteConfig.commonConfig.supportOpenAds || !RemoteConfig.commonConfig.isActiveAds) return
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd || AdManager.isShowInterOrReward) {
            Logger.d("The app open ad is already showing")
            return
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            Logger.d("The app open ad is not ready yet")
            loadAd(activity)
            TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_SHOW_FAIL_NO_ADS)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                // Called when full screen content is dismissed.
                // Set the reference to null so isAdAvailable() returns false.
                Logger.d("Open Ads: onAdDismissedFullScreenContent")
                appOpenAd = null
                isShowingAd = false
                loadAd(activity)
                EventBus.getDefault().post(OpenAdEvent(false))
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                // Set the reference to null so isAdAvailable() returns false.
                Logger.d("Open Ads: onAdFailedToShowFullScreenContent")
                appOpenAd = null
                loadAd(activity)
                TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_SHOW_FAIL)
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                EventBus.getDefault().post(OpenAdEvent(true))
                // Called when fullscreen content is shown.
                Logger.d("Open Ads: onAdShowedFullScreenContent")
                TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_SHOW_SUCCESS)
            }
        }
        appOpenAd?.show(activity)
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun release() {
        appOpenAd = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Handler(Looper.getMainLooper()).postDelayed({
            currentActivity?.get()?.let { showAdIfAvailable(it) }
        }, 500)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = WeakReference(p0)
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = WeakReference(p0)

    }

    override fun onActivityPaused(p0: Activity) {
        Logger.d("onActivityPaused")

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

}