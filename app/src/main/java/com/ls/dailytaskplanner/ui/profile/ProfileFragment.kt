package com.ls.dailytaskplanner.ui.profile

import android.annotation.SuppressLint
import com.google.android.material.timepicker.TimeFormat
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.base.BaseFragment
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.databinding.FragProfileBinding
import com.ls.dailytaskplanner.ui.MainActivity
import com.ls.dailytaskplanner.ui.dialog.AddTaskDialog
import com.ls.dailytaskplanner.ui.policy.PolicyFragment
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.TrackingHelper
import com.ls.dailytaskplanner.utils.setSafeOnClickListener
import com.ozcanalasalvar.datepicker.view.popup.TimePickerPopup
import com.ozcanalasalvar.datepicker.view.timepicker.TimePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
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
            TrackingHelper.logEvent(AllEvents.ACTION_CHANGE_NOTIFY_SOUND)
        }

        binding.swNotifyApp.setOnCheckedChangeListener { _, isChecked ->
            localStorage.enableNotifyApp = isChecked
            TrackingHelper.logEvent(AllEvents.ACTION_CHANGE_NOTIFY_OFFLINE)
        }

        binding.btnChooseTimeReminder.setSafeOnClickListener {
            TrackingHelper.logEvent(AllEvents.ACTION_CHANGE_TIME_REMIND_TASK)
            showTimePicker(TYPE_REMINDER)
        }

        binding.btnChooseTimeCreatePlan.setSafeOnClickListener {
            TrackingHelper.logEvent(AllEvents.ACTION_CHANGE_TIME_REMIND_NEW_PLAN)
            showTimePicker(TYPE_PLANNER)
        }

        binding.btnPolicy.setSafeOnClickListener {
            (activity as MainActivity?)?.addFragment(PolicyFragment())
        }

        binding.btnStatistic.setSafeOnClickListener {
            (activity as MainActivity?)?.addFragment(PolicyFragment())
        }

    }

    private fun showTimePicker(type: String) {
        val pickerPopup = TimePickerPopup.Builder()
            .from(context)
            .offset(1)
            .textSize(14)
            .setTime(
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE)
            )
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .darkModeEnabled(false)
            .listener(object : TimePickerPopup.TimeSelectListener {
                @SuppressLint("SetTextI18n", "DefaultLocale")
                override fun onTimeSelected(
                    timePicker: TimePicker?,
                    hour: Int,
                    minute: Int,
                    format: String?
                ) {
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    if (type == TYPE_REMINDER) {
                        binding.tvTimeReminder.text =
                            getString(R.string.remind_task) + " " + formattedTime.split(":")[0] + " " + getString(
                                R.string.minutes
                            )
                        localStorage.remindTaskBefore = formattedTime.split(":")[0]
                    } else {
                        localStorage.remindCreatePlan = formattedTime
                        binding.tvTimeCreatePlan.text =
                            getString(R.string.time_reminder_create_plan) + " " + formattedTime
                    }
                }
            })
            .build()

        pickerPopup.show(childFragmentManager, AddTaskDialog.TAG)
    }

    @SuppressLint("SetTextI18n")
    override fun viewCreated() {

        TrackingHelper.logEvent(AllEvents.VIEW_PROFILE)
        binding.swReminderSound.isChecked = localStorage.enableSoundNotify

        binding.swNotifyApp.isChecked = localStorage.enableNotifyApp

        binding.tvTimeCreatePlan.text =
            getString(R.string.time_reminder_create_plan) + " " + localStorage.remindCreatePlan
        binding.tvTimeReminder.text =
            getString(R.string.remind_task) + " " + localStorage.remindTaskBefore + " " + getString(
                R.string.minutes
            )
    }

    companion object {
        const val TAG = "ProfileFragment"
        const val TYPE_PLANNER = "planner"
        const val TYPE_REMINDER = "reminder_task"
    }
}