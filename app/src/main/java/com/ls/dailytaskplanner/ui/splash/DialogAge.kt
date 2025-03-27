package com.ls.dailytaskplanner.ui.splash

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.nativead.NativeAdView
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.base.BaseFragment
import com.ls.dailytaskplanner.databinding.DialogAnswerAgeBinding
import com.ls.dailytaskplanner.utils.RemoteConfig
import com.ls.dailytaskplanner.utils.TrackingHelper
import com.ls.dailytaskplanner.utils.setSafeOnClickListener
import com.ls.dailytaskplanner.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class DialogAge : BaseFragment<DialogAnswerAgeBinding, AgeViewModel>() {


    var onConfirm: (() -> Unit)? = null
    var onDeny: (() -> Unit)? = null
    private var age = ""
    private var didSelectedAge = false

    companion object {
        const val TAG = "DialogAge"
        const val UNDER_EIGHTEEN = "18"
        const val EIGHTEEN_TO_TWENTY_FOUR = "1824"
        const val TWENTY_FIVE_TO_THIRTY_FOUR = "2534"
        const val THIRTY_FIVE_TO_FORTY_FOUR = "3544"
        const val FORTY_FIVE = "45"
        var isDestroyDialogAge = false

        @JvmStatic
        fun newInstance(
            onConfirm: (() -> Unit)? = null, onDeny: (() -> Unit)? = null
        ): DialogAge {
            val dialog = DialogAge()
            dialog.onConfirm = onConfirm
            dialog.onDeny = onDeny
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId() = R.layout.dialog_answer_age

    override fun observersSomething() {
        AdManager.nativeAgeLiveData.observe(viewLifecycleOwner) {
            if (activity == null || AdManager.nativeAgeLiveData.value == null) return@observe
            lifecycleScope.launch(Dispatchers.Main) {
                delay(300)
                binding.containerNativeAge.visible()
                binding.layoutAdsAb.visible()
                val frameLayout: FrameLayout = binding.layoutAdsAb
                val adView = LayoutInflater.from(context).inflate(
                    R.layout.native_add_task, null
                ) as NativeAdView
                AdManager.populateUnifiedNativeAdView(it, adView)
                frameLayout.removeAllViews()
                frameLayout.addView(adView)
            }

        }
    }

    private fun updateNativeAgeV2() {
        if (activity == null || AdManager.nativeAge2LiveData.value == null) return

        lifecycleScope.launch(Dispatchers.Main) {
            delay(300)
            binding.containerNativeAge.visible()
            binding.layoutAdsAb.visible()
            val frameLayout: FrameLayout = binding.layoutAdsAb
            val adView = LayoutInflater.from(context).inflate(
                R.layout.native_add_task, null
            ) as NativeAdView
            AdManager.populateUnifiedNativeAdView(AdManager.nativeAge2LiveData.value, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }
    }

    override fun bindingAction() {

    }

    override fun viewCreated() {
        TrackingHelper.logEvent("view_age")
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        isDestroyDialogAge = true
    }


    private fun setup() {
        binding.txtTitle.text = getString(R.string.my_age)
        binding.btnContinue.text = getString(R.string.msg_btn_continue)
        binding.btnAgeOne.text = getString(R.string.under18)
        binding.btnAgeTow.text = getString(R.string.age1824)
        binding.btnAgeThree.text = getString(R.string.age2435)
        binding.btnAgeFour.text = getString(R.string.age3544)
        binding.btnAgeFive.text = getString(R.string.ageover45)
        resetDefaultBackground()

        binding.btnAgeOne.setSafeOnClickListener {
            setBackgroundSelected(binding.btnAgeOne)
            age = UNDER_EIGHTEEN
        }

        binding.btnAgeTow.setSafeOnClickListener {
            setBackgroundSelected(binding.btnAgeTow)
            age = EIGHTEEN_TO_TWENTY_FOUR
        }

        binding.btnAgeThree.setSafeOnClickListener {
            setBackgroundSelected(binding.btnAgeThree)
            age = TWENTY_FIVE_TO_THIRTY_FOUR
        }

        binding.btnAgeFour.setSafeOnClickListener {
            setBackgroundSelected(binding.btnAgeFour)
            age = THIRTY_FIVE_TO_FORTY_FOUR
        }

        binding.btnAgeFive.setSafeOnClickListener {
            setBackgroundSelected(binding.btnAgeFive)
            age = FORTY_FIVE
        }

        binding.btnContinue.setSafeOnClickListener {
            if (didSelectedAge) {
                onConfirm?.invoke()
            } else Toast.makeText(
                requireContext(), getString(R.string.msg_please_select_age), Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setBackgroundSelected(view: AppCompatTextView) {
        if (RemoteConfig.commonConfig.highlightButtonNext) {
            binding.btnContinue.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.background_btn_intro,
                    null
                )
            )
        }
        binding.btnContinue.visible()
        updateNativeAgeV2()
        didSelectedAge = true
        resetDefaultBackground()
        backGroundChoose(view)
        view.typeface = Typeface.DEFAULT_BOLD
        when (view.id) {
            R.id.btnAgeOne -> showView(binding.imgChooseOne)
            R.id.btnAgeTow -> showView(binding.imgChooseTwo)
            R.id.btnAgeThree -> showView(binding.imgChooseThree)
            R.id.btnAgeFour -> showView(binding.imgChooseFour)
            R.id.btnAgeFive -> showView(binding.imgChooseFive)
        }
    }

    private fun resetDefaultBackground() {
        backGroundNotChoose(binding.btnAgeOne)
        backGroundNotChoose(binding.btnAgeTow)
        backGroundNotChoose(binding.btnAgeThree)
        backGroundNotChoose(binding.btnAgeFour)
        backGroundNotChoose(binding.btnAgeFive)
        hideView(binding.imgChooseTwo)
        hideView(binding.imgChooseThree)
        hideView(binding.imgChooseFour)
        hideView(binding.imgChooseOne)
        hideView(binding.imgChooseFive)
        binding.btnAgeOne.typeface = Typeface.DEFAULT
        binding.btnAgeTow.typeface = Typeface.DEFAULT
        binding.btnAgeThree.typeface = Typeface.DEFAULT
        binding.btnAgeFour.typeface = Typeface.DEFAULT
        binding.btnAgeFive.typeface = Typeface.DEFAULT
    }

    private fun backGroundNotChoose(view: View) {
        view.setBackgroundResource(R.drawable.bg_option_age_ab)
    }

    private fun backGroundChoose(view: View) {
        view.setBackgroundResource(R.drawable.bg_btn_age)
    }

    private fun hideView(view: View) {
        view.visibility = View.GONE
    }

    private fun showView(view: View) {
        view.visibility = View.VISIBLE
    }


}