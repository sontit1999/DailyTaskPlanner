package com.example.dailytaskplanner.ui.profile

import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.base.BaseFragment
import com.example.dailytaskplanner.databinding.FragProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragProfileBinding, ProfileViewModel>() {

    override fun getLayoutId() = R.layout.frag_profile

    override fun observersSomething() {
    }

    override fun bindingAction() {

    }

    override fun viewCreated() {

    }
}