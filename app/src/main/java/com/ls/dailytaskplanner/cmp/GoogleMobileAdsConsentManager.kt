package com.ls.dailytaskplanner.cmp

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import com.ls.dailytaskplanner.utils.Logger
import java.util.concurrent.atomic.AtomicBoolean

class GoogleMobileAdsConsentManager  constructor(
    context: Context,
) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    /** Interface definition for a callback to be invoked when consent gathering is complete. */
    fun interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    /** Helper variable to determine if the app can request ads. */
    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    private val isShownConsentForm= AtomicBoolean(false)

    /** Helper variable to determine if the privacy options form is required. */
    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener,
    ) {
        if (isShownConsentForm.get()) {
            Logger.d("${Companion.TAG} ##### BAILS shown one time")
            onConsentGatheringCompleteListener.consentGatheringComplete(null)
            return
        }
        // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
        val debugSettings =
            ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                // Check your logcat output for the hashed device ID e.g.
                // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345")" to use
                // the debug functionality.
                .addTestDeviceHashedId("D011681FFA31E4804FADA5EE3BFB3D89")
                .build()

        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        // Requesting an update to consent information should be called on every app launch.
        Logger.d("$TAG ##### START requestConsentInfoUpdate")
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                Logger.d("$TAG ##### FINISH requestConsentInfoUpdate")
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    Logger.d("$TAG ##### FINISH show Consent Form")
                    isShownConsentForm.set(true)
                    // Consent has been gathered.
                    onConsentGatheringCompleteListener.consentGatheringComplete(formError)
                }
            },
            { requestConsentError ->
                Logger.d("$TAG ##### ERROR RequestConsentInfo")
                onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
            }
        )
    }

    /** Helper method to call the UMP SDK method to show the privacy options form. */
    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener,
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    companion object {
        private const val TAG = "ConsentManager"
    }
}
