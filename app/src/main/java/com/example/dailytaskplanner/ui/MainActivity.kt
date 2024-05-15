package com.example.dailytaskplanner.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.adapter.MainPagerAdapter
import com.example.dailytaskplanner.databinding.ActivityMainBinding


class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        setUpViewPager()
        setUpBottomNavigation()
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


}
