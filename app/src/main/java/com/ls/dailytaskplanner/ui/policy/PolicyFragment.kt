package com.ls.dailytaskplanner.ui.policy

import android.annotation.SuppressLint
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.base.BaseFragment
import com.ls.dailytaskplanner.databinding.FragPolicyBinding
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyFragment : BaseFragment<FragPolicyBinding, PolicyViewModel>() {

    override fun getLayoutId() = R.layout.frag_policy

    override fun observersSomething() {
    }

    override fun bindingAction() {

    }


    @SuppressLint("SetTextI18n")
    override fun viewCreated() {
        binding.backBtn.setSafeOnClickListener {
            activity?.onBackPressed()
        }
        binding.tvPolicy.text = context?.let { AppUtils.readFileTextFromAssets(it, "policy.txt") }
    }

    companion object {
        const val TAG = "PolicyFragment"

    }
}