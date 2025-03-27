package com.ls.dailytaskplanner.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.adapter.MainPagerAdapter
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.cmp.GoogleMobileAdsConsentManager
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.databinding.ActivityMainBinding
import com.ls.dailytaskplanner.model.eventbus.OpenAdEvent
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.Constants
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.MediaPlayerManager
import com.ls.dailytaskplanner.utils.NotificationUtils
import com.ls.dailytaskplanner.utils.RemoteConfig
import com.ls.dailytaskplanner.utils.TrackingHelper
import com.ls.dailytaskplanner.utils.gone
import com.ls.dailytaskplanner.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdManager.loadOpenAd(this)
        storage.didChooseLanguage = true
        EventBus.getDefault().register(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        checkPermissionNotifyAndroid13()
        setUpViewPager()
        setUpBottomNavigation()
        loadBannerAd()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Logger.d(TAG, "Allow permission notify !")
                AppUtils.startTaskService()
                TrackingHelper.logEvent(AllEvents.ACCEPT_PERMISSION_NOTIFY)
            } else {
                Logger.d(TAG, "Deny permission notify !")
                TrackingHelper.logEvent(AllEvents.DECLINE_PERMISSION_NOTIFY)
                showSettingSystem()
            }
        }

    private fun checkPermissionNotifyAndroid13() {
        val isGranted = AppUtils.hasPostNotifyPermissions(this)
        if (!isGranted) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            AppUtils.startTaskService()
        }
    }

    //Show System Setting
    private var confirmNotifyActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //  you will get result here in result.data
            val isGranted = AppUtils.hasPostNotifyPermissions(this)
            if (!isGranted) {
                TrackingHelper.logEvent(AllEvents.DECLINE_PERMISSION_NOTIFY)
            } else {
                TrackingHelper.logEvent(AllEvents.ACCEPT_PERMISSION_NOTIFY)
                AppUtils.startTaskService()
            }
        }
    }

    private fun showSettingSystem() {
        isOpenSettingSystem = true
        TrackingHelper.logEvent(AllEvents.OPEN_SETTING_NOTIFY)
        val settingsIntent: Intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        confirmNotifyActivityResult.launch(settingsIntent)
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


    private fun loadBannerAd() {
        AdManager.loadBanner(
            view = binding.containerAd,
            adKey = RemoteConfig.commonConfig.bannerAdKey,
            isShowCollapsible = false,
            autoRefresh = false,
            nameScreen = "home"
        )
    }

    private fun setUpBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    binding.viewPager.currentItem = 0
                    true
                }

                R.id.menu_personal -> {
                    binding.viewPager.currentItem = 1
                    true
                }

                else -> false
            }
        }
    }

    private fun setUpViewPager() {
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = MainPagerAdapter(this)
    }

    @Subscribe
    fun openAdEvent(event: OpenAdEvent) {
        if (event.isShow) {
            binding.containerAd.gone()
        } else {
            binding.containerAd.visible()
        }
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

    }
}
