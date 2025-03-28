package com.ls.dailytaskplanner.ui.intro

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.nativead.NativeAdView
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.base.BaseFragment
import com.ls.dailytaskplanner.databinding.FragIntroBinding
import com.ls.dailytaskplanner.ui.splash.SplashActivity
import com.ls.dailytaskplanner.utils.RemoteConfig
import com.ls.dailytaskplanner.utils.setSafeOnClickListener
import com.ls.dailytaskplanner.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroFragment : BaseFragment<FragIntroBinding, IntroViewModel>() {

    override fun getLayoutId() = R.layout.frag_intro

    override fun observersSomething() {
    }

    override fun bindingAction() {

    }


    @SuppressLint("SetTextI18n")
    override fun viewCreated() {
        setUpView()
    }

    private fun setUpView() {
        if (RemoteConfig.commonConfig.highlightButtonNext) {
            binding.btnNext.setTextColor(Color.WHITE)
            binding.btnNext.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.background_btn_intro,
                    null
                )
            )
        }
        val typeIntro = requireArguments().getInt(KEY_TYPE_INTRO, TYPE_INTRO_1)
        if (typeIntro == TYPE_INTRO_1) {
            binding.imgViewOne.setAnimation(R.raw.gift)
            binding.imgViewOne.playAnimation()
            AdManager.nativeIntro1.observe(viewLifecycleOwner) {
                if (activity == null || AdManager.nativeIntro1.value == null) return@observe
                lifecycleScope.launch(Dispatchers.Main) {
                    val frameLayout: FrameLayout = binding.layoutAds
                    val adView = LayoutInflater.from(context).inflate(
                        R.layout.native_intro1, null
                    ) as NativeAdView
                    AdManager.populateUnifiedNativeAdView(it, adView)
                    frameLayout.removeAllViews()
                    frameLayout.addView(adView)
                }

            }

            binding.btnNext.setSafeOnClickListener {
                (requireActivity() as? SplashActivity?)?.addFragment(
                    newInstances(
                        getString(R.string.effective_plan),
                        getString(R.string.notify_saturday),
                        TYPE_INTRO_2
                    )
                )
            }
        } else {
            binding.imgViewOne.setAnimation(R.raw.star)
            binding.imgViewOne.playAnimation()

            AdManager.nativeIntro2.observe(viewLifecycleOwner) {
                if (activity == null || AdManager.nativeIntro2.value == null) return@observe

                lifecycleScope.launch(Dispatchers.Main) {
                    binding.layoutAds.visible()
                    val frameLayout: FrameLayout = binding.layoutAds
                    val adView = LayoutInflater.from(context).inflate(
                        R.layout.native_intro1, null
                    ) as NativeAdView
                    AdManager.populateUnifiedNativeAdView(it, adView)
                    frameLayout.removeAllViews()
                    frameLayout.addView(adView)
                }

            }
            binding.btnNext.setSafeOnClickListener {
                (requireActivity() as? SplashActivity?)?.showAgeScreen()
            }
        }
        requireArguments().getString(KEY_TITLE)?.let {
            binding.tvTitle.text = it
        }
        requireArguments().getString(KEY_MESSAGE)?.let {
            binding.tvMessage.text = it
        }
        binding.btnNext.text = getString(R.string.msg_btn_continue)

    }

    companion object {

        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        private const val KEY_TYPE_INTRO = "key_intro"
        const val TYPE_INTRO_1 = 1
        const val TYPE_INTRO_2 = 2

        fun newInstances(title: String, description: String, typeIntro: Int): IntroFragment {
            val frag = IntroFragment()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putString(KEY_MESSAGE, description)
            bundle.putInt(KEY_TYPE_INTRO, typeIntro)
            frag.arguments = bundle
            return frag
        }
    }
}