package com.ls.dailytaskplanner.ui.splash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.ads.AppOpenAdManager
import com.ls.dailytaskplanner.cmp.GoogleMobileAdsConsentManager
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.databinding.ActivitySplashBinding
import com.ls.dailytaskplanner.model.ConfigModel
import com.ls.dailytaskplanner.ui.MainActivity
import com.ls.dailytaskplanner.ui.intro.IntroFragment
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.Constants
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.MediaPlayerManager
import com.ls.dailytaskplanner.utils.NotificationUtils
import com.ls.dailytaskplanner.utils.RemoteConfig
import com.ls.dailytaskplanner.utils.TrackingHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : FragmentActivity() {

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        checkIntent(intent)
        checkConsent()
        getConfigRemote()
        trackingOpenApp()
        listenerNetwork()
    }

    private fun initMobileAd() {
        MobileAds.initialize(
            this
        ) {
            Logger.d("-----> MobileAds initialized")
            loadBannerAd()
            loadNativeAges()
            handleLoadShowAdSplash()
            AdManager.initialize()
        }
    }

    private fun loadNativeAges() {
        if (!storage.didChooseLanguage) {
            AdManager.loadNativeAge()
            AdManager.loadNativeAge2()
            AdManager.loadNativeIntro1()
            AdManager.loadNativeIntro2()
        }
    }

    private fun checkConsent() {
        if (googleMobileAdsConsentManager.canRequestAds) {
            AppUtils.canRequestAd = true
            initMobileAd()
            return
        }
        googleMobileAdsConsentManager.gatherConsent(this) {
            AppUtils.canRequestAd = true
            if (googleMobileAdsConsentManager.canRequestAds) {
                initMobileAd()
                return@gatherConsent
            } else {
                gotoMainActivity()
            }
        }
    }


    private fun getConfigRemote() {
        MainScope().launch(Dispatchers.IO) {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings =
                FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(10).build()

            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate().addOnCompleteListener { task: Task<Boolean?> ->
                if (task.isSuccessful) {
                    setUpConfigRemote(remoteConfig)
                } else {
                    setUpConfigDefault()
                }
            }
        }
    }

    private fun setUpConfigDefault() {
        TrackingHelper.logEvent(AllEvents.CONFIG_LOAD_FAIL)
        RemoteConfig.configModel = ConfigModel.newInstance("")
    }

    private fun setUpConfigRemote(remoteConfig: FirebaseRemoteConfig) {
        TrackingHelper.logEvent(AllEvents.CONFIG_LOAD_SUCCESS)
        Logger.d(TAG, "Fetch config remote success: " + remoteConfig.all)
        var jsonConfig = ""
        jsonConfig = if (BuildConfig.DEBUG) {
            remoteConfig.getString("config_debug")
        } else {
            remoteConfig.getString("config")
        }
        Logger.d(TAG, "jsonConfig = $jsonConfig")
        val configModel: ConfigModel = ConfigModel.newInstance(jsonConfig)
        RemoteConfig.configModel = configModel
        storage.timeCheckStatusTask = RemoteConfig.commonConfig.timeCheckTask
        try {
            if (getVersionCode(this) == RemoteConfig.commonConfig.versionCodeForReview) {
                RemoteConfig.commonConfig.resetAd()
            }
        } catch (e: Exception) {
            Logger.e(e.message.toString())
        }
    }

    private fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionCode
    }

    private fun listenerNetwork() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), object :
            NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isNetworkAvailable = true
                Logger.d(TAG, "Internet available")
                TrackingHelper.logEvent(AllEvents.AVAILABLE_INTERNET)
                // Do something when the network becomes available
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isNetworkAvailable = false
                Logger.d(TAG, "Internet lost")
                TrackingHelper.logEvent(AllEvents.LOST_INTERNET)
                // Do something when the network is lost
            }
        })
    }

    private fun trackingOpenApp() {
        val countOpenApp = storage.openCount
        if (countOpenApp == 0) {
            TrackingHelper.logEvent(AllEvents.USER_FIRST_OPEN)
        } else {
            TrackingHelper.logEvent(AllEvents.USER_REOPEN)
        }
        storage.openCount = countOpenApp + 1
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent) {
        val typeNotify = intent.getIntExtra(Constants.IntentKey.TYPE_NOTIFY, -1)
        Logger.d(TAG, "---> hihi type notify = $typeNotify")
        when (typeNotify) {
            -1 -> {

            }

            NotificationUtils.NOTIFICATION_ID_SERVICE -> {

            }

            NotificationUtils.NOTIFICATION_ID_REMIND_TASK -> {
                TrackingHelper.logEvent(AllEvents.NOTIFY_REMIND_TASK + "open")
            }

            NotificationUtils.NOTIFY_DAILY_OFFLINE -> {
                TrackingHelper.logEvent(AllEvents.NOTIFY_DAILY + "open")
            }

            NotificationUtils.NOTIFICATION_ID_UPDATE_STATUS_TASK -> {
                TrackingHelper.logEvent(AllEvents.NOTIFY_UPDATE_STATUS_TASK + "open")
            }

            NotificationUtils.NOTIFY_SATURDAY -> {
                TrackingHelper.logEvent(AllEvents.NOTIFY_SATURDAY + "open")
            }

            NotificationUtils.NOTIFICATION_ID_REMIND_CREATE_PLAN_TODAY -> {
                TrackingHelper.logEvent(AllEvents.NOTIFY_INVITE_CREATE_PLAN + "open")
            }
        }
    }


    private fun handleLoadShowAdSplash() {
        AdManager.handleLoadInterSplash(onLoadFinish = {
            if (AdManager.interSplash != null) {
                AdManager.showInterSplash(onFinish = {
                    gotoMainActivity()
                }, activity = this@SplashActivity)
            } else {
                AdManager.loadOpenAdSplash { openAd ->
                    if (openAd != null) {
                        openAd.fullScreenContentCallback = object : FullScreenContentCallback() {

                            override fun onAdDismissedFullScreenContent() {
                                // Called when full screen content is dismissed.
                                // Set the reference to null so isAdAvailable() returns false.
                                gotoMainActivity()
                                AppOpenAdManager.isShowingOpenAdOpenApp = false
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                gotoMainActivity()
                                TrackingHelper.logEvent(AllEvents.OPEN_ADS_SPLASH_SHOW_FAIL)
                            }

                            override fun onAdShowedFullScreenContent() {
                                AppOpenAdManager.isShowingOpenAdOpenApp = true
                                TrackingHelper.logEvent(AllEvents.OPEN_ADS_SPLASH_SHOW_SUCCESS)
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                TrackingHelper.logEvent(AllEvents.OPEN_ADS_SPLASH_CLICK)
                            }
                        }
                        openAd.show(this)
                    } else {
                        TrackingHelper.logEvent(AllEvents.OPEN_ADS_SPLASH_SHOW_FAIL_NO_ADS)
                        gotoMainActivity()
                    }
                }
            }
        }, isHighFloor = true)

    }

    fun gotoMainActivity() {
        if (!storage.didChooseLanguage) {
            addFragment(
                IntroFragment.newInstances(
                    getString(R.string.effective_plan),
                    getString(R.string.notify_saturday),
                    IntroFragment.TYPE_INTRO_1
                )
            )
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun showAgeScreen() {
        addFragment(DialogAge.newInstance(onConfirm = {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, onDeny = {

        }))
    }

    private fun loadBannerAd() {
        AdManager.loadBanner(
            view = binding.containerAd,
            adKey = RemoteConfig.commonConfig.bannerSplashAdKey,
            isShowCollapsible = false,
            autoRefresh = false,
            nameScreen = "home"
        )
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        ).add(R.id.containerFragment, fragment)
            .addToBackStack(fragment.javaClass.simpleName).commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.isEmpty()) return
        if (supportFragmentManager.fragments.last() is IntroFragment) return
        if (supportFragmentManager.fragments.last() is DialogAge) return
        super.onBackPressed()
    }


    override fun onDestroy() {
        super.onDestroy()
        Logger.d(TAG, "onDestroy Main")
        EventBus.getDefault().unregister(this)
        MediaPlayerManager.stopPlaying()
        AdManager.destroyAll()
    }

    companion object {
        private const val TAG = "MainActivity"
        var isOpenSettingSystem = false
        var isNetworkAvailable = false
    }
}
