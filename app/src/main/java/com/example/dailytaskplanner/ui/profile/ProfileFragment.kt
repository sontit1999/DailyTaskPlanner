package com.example.dailytaskplanner.ui.profile

import android.annotation.SuppressLint
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.base.BaseFragment
import com.example.dailytaskplanner.database.storage.LocalStorage
import com.example.dailytaskplanner.databinding.FragProfileBinding
import com.example.dailytaskplanner.ui.dialog.AddTaskDialog
import com.example.dailytaskplanner.utils.setSafeOnClickListener
import com.google.android.material.timepicker.TimeFormat
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
        }

        binding.swNotifyApp.setOnCheckedChangeListener { _, isChecked ->
            localStorage.enableNotifyApp = isChecked
        }

        binding.btnChooseTimeReminder.setSafeOnClickListener {
            showTimePicker(TYPE_REMINDER)
        }

        binding.btnChooseTimeCreatePlan.setSafeOnClickListener {
            showTimePicker(TYPE_PLANNER)
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