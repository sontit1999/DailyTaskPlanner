package com.example.dailytaskplanner.ads

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.dailytaskplanner.App
import com.example.dailytaskplanner.model.eventbus.InterAdEvent
import com.example.dailytaskplanner.utils.AllEvents
import com.example.dailytaskplanner.utils.AppConfig
import com.example.dailytaskplanner.utils.Logger
import com.example.dailytaskplanner.utils.RemoteConfig
import com.example.dailytaskplanner.utils.TrackingHelper
import com.example.dailytaskplanner.utils.gone
import com.example.dailytaskplanner.utils.visible
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import org.greenrobot.eventbus.EventBus
import java.io.IOException

object AdManager {
    const val TAG_BACK_INTER_PDF = "TAG_BACK_INTER_PDF"
    const val TAG_BACK_INTER_ALL = "TAG_BACK_INTER_ALL"

    private var isDoingLoadInter = false
    private var interstitialAd: InterstitialAd? = null
    private var showedInterstitialLastTime = 0L
    var isShowInterOrReward = false

    fun initialize() {
        AppOpenAdManager.start()
    }

    fun loadBanner(view: FrameLayout): AdView? {
        view.gone()
        if (!RemoteConfig.commonConfig.isActiveAds || !RemoteConfig.commonConfig.supportBanner) return null
        try {
            val adView = AdView(view.context)
            adView.adListener = object : AdListener() {

                override fun onAdClicked() {
                    super.onAdClicked()
                    TrackingHelper.logEvent(AllEvents.E1_ADS_BANNER_CLICK)
                }

                override fun onAdLoaded() {
                    view.visible()
                    view.removeAllViews()
                    val params = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.gravity = Gravity.BOTTOM
                    view.addView(adView, params)
                    TrackingHelper.logEvent(AllEvents.E1_ADS_BANNER_LOAD_SUCCESS)
                    Logger.d("Banner load success")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    view.gone()
                    TrackingHelper.logEvent(AllEvents.E1_ADS_BANNER_LOAD_FAIL)
                    Logger.d("Banner load fail: ${p0.message}")
                    super.onAdFailedToLoad(p0)
                }
            }
            adView.adUnitId = RemoteConfig.commonConfig.bannerAdKey
            adView.setAdSize(
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    App.mInstance,
                    (AppConfig.widthScreen / AppConfig.displayMetrics.density).toInt()
                )
            )
            adView.loadAd(buildAdRequest())
            return adView
        } catch (e: IOException) {
            Logger.d("Banner load fail: ${e.message}")
        }
        return null
    }

    private fun isInterAvailable() = interstitialAd != null

    private fun handleLoadInter() {
        if (!RemoteConfig.commonConfig.supportInter || !RemoteConfig.commonConfig.isActiveAds) return
        if (isInterAvailable()) return
        if (isDoingLoadInter) return

        InterstitialAd.load(
            App.mInstance,
            RemoteConfig.commonConfig.interAdKey,
            buildAdRequest(), object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    interstitialAd = p0
                    isDoingLoadInter = false
                    TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_LOAD_SUCCESS)
                    Logger.d("Inter ads load success")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    isDoingLoadInter = false
                    interstitialAd = null
                    TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_LOAD_FAIL)
                    Logger.d("Inter ads load fail:${p0.message}")
                }
            })
        isDoingLoadInter = true
    }

    fun showInter(
        isForced: Boolean = false, tag: String, onHidden: (() -> Unit)? = null
    ): Boolean {
        if (!RemoteConfig.commonConfig.supportInter || !RemoteConfig.commonConfig.isActiveAds) return false
        val activity = AppOpenAdManager.currentActivity?.get()
        activity ?: return false
        return if (canShowInter || isForced) {
            if (interstitialAd == null) {
                TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_SHOW_FAIL_NO_ADS)
                Logger.d("Inter show fail because inter = null")
                handleLoadInter()
                false
            } else {
                interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {

                    override fun onAdClicked() {
                        super.onAdClicked()
                        TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_CLICKED)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        onHidden?.invoke()
                        EventBus.getDefault().post(InterAdEvent(false, tag))
                        interstitialAd = null
                        handleLoadInter()
                        TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_SHOW_FAIL)
                        Logger.d("Inter show fail ")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        onHidden?.invoke()
                        EventBus.getDefault().post(InterAdEvent(false, tag))
                        isShowInterOrReward = false
                        showedInterstitialLastTime = System.currentTimeMillis()
                        interstitialAd = null
                        handleLoadInter()
                        Logger.d("Inter dismiss ")
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowInterOrReward = true
                        TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_SHOW_SUCCESS)
                        EventBus.getDefault().post(InterAdEvent(true, tag))
                        Logger.d("Inter show success ")
                    }

                }
                interstitialAd!!.show(activity)
                interstitialAd = null
                true
            }
        } else false
    }
    
    fun updateLastTimeShowInter(lastTime : Long){
        this.showedInterstitialLastTime = lastTime
    }

    private val canShowInter: Boolean
        get() {
            var milliseconds = RemoteConfig.commonConfig.waitingShowInter
            milliseconds *= 1000
            if (milliseconds < 0) return false
            val delta = System.currentTimeMillis() - showedInterstitialLastTime
            return delta > milliseconds || delta <= 0
        }


    private fun buildAdRequest(): AdRequest {
        val extras = Bundle()
        return AdRequest.Builder()
            .addNetworkExtrasBundle(
                AdMobAdapter::class.java,
                extras
            )
            .build()
    }
    //endregion

    fun loadAdIfNeed(context: Context) {
        handleLoadInter()
        AppOpenAdManager.loadAd(context)
    }

    fun destroyAll() {
        interstitialAd = null
        AppOpenAdManager.release()
    }
}