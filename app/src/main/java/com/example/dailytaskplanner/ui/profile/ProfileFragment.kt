package com.example.dailytaskplanner.ui.profile

import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.base.BaseFragment
import com.example.dailytaskplanner.database.storage.LocalStorage
import com.example.dailytaskplanner.databinding.FragProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragProfileBinding, ProfileViewModel>() {

    @Inject
    lateinit var localStorage: LocalStorage

    override fun getLayoutId() = R.layout.frag_profile

    override fun observersSomething() {
    }

    override fun bindingAction() {
        binding.swReminderSound.setOnCheckedChangeListener { _, isChecked ->
            localStorage.enableSoundNotify = isChecked
        }
    }

    override fun viewCreated() {

    }
}