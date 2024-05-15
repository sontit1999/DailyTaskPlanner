package com.example.dailytaskplanner.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dailytaskplanner.ui.home.HomeFragment
import com.example.dailytaskplanner.ui.profile.ProfileFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}