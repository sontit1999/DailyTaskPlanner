package com.ls.dailytaskplanner.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.nativead.NativeAdView
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.adapter.LanguageAdapter
import com.ls.dailytaskplanner.adapter.MainPagerAdapter
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.ads.AppOpenAdManager
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.databinding.ActivityMainBinding
import com.ls.dailytaskplanner.model.Language
import com.ls.dailytaskplanner.model.eventbus.OpenAdEvent
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.MediaPlayerManager
import com.ls.dailytaskplanner.utils.NotificationUtils
import com.ls.dailytaskplanner.utils.RemoteConfig
import com.ls.dailytaskplanner.utils.gone
import com.ls.dailytaskplanner.utils.invisible
import com.ls.dailytaskplanner.utils.setSafeOnClickListener
import com.ls.dailytaskplanner.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var storage: LocalStorage

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        EventBus.getDefault().register(this)
        AdManager.initialize()
        AppUtils.startTaskService()
        setUpViewPager()
        setUpBottomNavigation()
        initRvLanguage()
        handleLoadShowOpenAd()
    }

    private fun initRvLanguage() {
        if (storage.didChooseLanguage) return
        binding.btnContinueAb.setSafeOnClickListener {
            storage.didChooseLanguage = true
            hiddenLayoutLanguage()
        }
        val adapter = LanguageAdapter(this)
        binding.rvLanguage.adapter = adapter
        binding.rvLanguage.setHasFixedSize(true)
        binding.rvLanguage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.setData(
            mutableListOf(
                Language(R.drawable.us, "English", "en"),
                Language(R.drawable.vn, "Tiếng việt", "vi"),
                Language(R.drawable.pt, "Português", "pt"),
                Language(R.drawable.mx, "Español", "es"),
                Language(R.drawable.ic_polish, "Język polski", "pl"),
                Language(R.drawable.ic_cezch, "čeština", "cs"),
                Language(R.drawable.ic_de, "Deutsch", "de"),
                Language(R.drawable.fr, "Français", "fr"),
                Language(R.drawable.jp, "日本語", "ja"),
                Language(R.drawable.kr, "한국어", "ko")
            )
        )

        AdManager.nativeAgeLiveData.observe(this) {
            if (it != null) {
                binding.containerAdNative.visible()
                val frameLayout: FrameLayout = binding.containerAdNative
                val adView = LayoutInflater.from(this).inflate(
                    R.layout.native_add_task, null
                ) as NativeAdView
                AdManager.populateUnifiedNativeAdView(it, adView)
                frameLayout.removeAllViews()
                frameLayout.addView(adView)
            }
        }
    }

    private fun hiddenLayoutLanguage() {
        AdManager.loadAdIfNeed(this)
        AdManager.showInterSplash(onFinish = {
            loadBannerAd()
            binding.layoutLanguage.gone()
        }, this)
    }

    private fun handleLoadShowOpenAd() {
        AdManager.loadOpenAdSplash { openAd ->
            if (openAd != null) {
                openAd.fullScreenContentCallback = object : FullScreenContentCallback() {

                    override fun onAdDismissedFullScreenContent() {
                        // Called when full screen content is dismissed.
                        // Set the reference to null so isAdAvailable() returns false.
                        hiddenSplash()
                        AppOpenAdManager.isShowingOpenAdOpenApp = false
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        hiddenSplash()
                    }

                    override fun onAdShowedFullScreenContent() {
                        AppOpenAdManager.isShowingOpenAdOpenApp = true
                    }
                }
                openAd.show(this)
            } else {
                hiddenSplash()
            }
        }
    }

    fun hiddenSplash() {
        binding.layoutSplash.gone()
        if (!storage.didChooseLanguage) {
            binding.layoutLanguage.visible()
            AdManager.handleLoadInterSplash()
        } else {
            loadBannerAd()
            AdManager.loadAdIfNeed(this)
        }
    }

    private fun loadBannerAd() {
        AdManager.loadBanner(binding.containerAd, RemoteConfig.commonConfig.bannerAdKey, true)
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
            binding.containerAdNative.invisible()
        } else {
            binding.containerAd.visible()
            binding.containerAdNative.visible()
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        NotificationUtils.scheduleReminderNotification(this)
    }

    override fun onResume() {
        super.onResume()
        NotificationUtils.cancelReminderNotification(this)
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
    }
}
