package com.ls.dailytaskplanner.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.adapter.MainPagerAdapter
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.databinding.ActivityMainBinding
import com.ls.dailytaskplanner.model.eventbus.OpenAdEvent
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.NotificationUtils
import com.ls.dailytaskplanner.utils.RemoteConfig
import com.ls.dailytaskplanner.utils.gone
import com.ls.dailytaskplanner.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        AdManager.initialize()
        AppUtils.startTaskService()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        setUpViewPager()
        setUpBottomNavigation()
        loadBannerAd()
        AdManager.loadAdIfNeed(this)
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
        } else binding.containerAd.visible()
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
        EventBus.getDefault().unregister(this)
        AdManager.destroyAll()
    }

}
