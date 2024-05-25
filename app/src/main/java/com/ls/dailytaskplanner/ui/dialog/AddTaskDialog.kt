package com.ls.dailytaskplanner.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.adapter.ChooseColorAdapter
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.ads.AdManager.TAG_INTER_ADD_TASK
import com.ls.dailytaskplanner.databinding.DialogAddTaskBinding
import com.ls.dailytaskplanner.model.Task
import com.ls.dailytaskplanner.model.eventbus.InterAdEvent
import com.ls.dailytaskplanner.model.eventbus.OpenAdEvent
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.AppUtils.hiddenKeyboard
import com.ls.dailytaskplanner.utils.gone
import com.ls.dailytaskplanner.utils.setSafeOnClickListener
import com.ls.dailytaskplanner.utils.visible
import com.ozcanalasalvar.datepicker.utils.DateUtils.getCurrentTime
import com.ozcanalasalvar.datepicker.view.popup.DatePickerPopup
import com.ozcanalasalvar.datepicker.view.popup.TimePickerPopup
import com.ozcanalasalvar.datepicker.view.timepicker.TimePicker
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.Calendar

@AndroidEntryPoint
class AddTaskDialog : DialogFragment() {

    lateinit var binding: DialogAddTaskBinding

    private val viewModel: AddTaskViewModel by viewModels()

    private lateinit var chooseColorAdapter: ChooseColorAdapter

    private var taskEdit: Task? = null
    private var dateCreateTask = AppUtils.getCurrentDate()
    var didShowNativeAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        arguments?.let {
            val task = Gson().fromJson(it.getString(KEY_TASK), Task::class.java)
            dateCreateTask = it.getString(KEY_DATE, "")
            task?.let {
                taskEdit = it
                viewModel.task = it
            }
            if (dateCreateTask.isNotEmpty()) {
                viewModel.task.dateStart = dateCreateTask
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Make the dialog full screen
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setWindowAnimations(R.style.DialogAnimation)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_task, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initRecyclerView()
        bindingAction()
        setUpObserver()
        loadNativeAd()
    }

    private fun loadNativeAd() {

    }

    private fun initData() {
        taskEdit?.let {
            if (it.color.isNotEmpty()) {
                binding.container.setBackgroundColor(Color.parseColor(it.color))
                binding.nestedScrollView.setBackgroundColor(Color.parseColor(it.color))
            }
            if (it.dateStart.isNotEmpty()) {
                binding.tvDate.text = it.dateStart
            }
            if (it.timeStart.isNotEmpty() && it.timeStart != "00:00") {
                binding.tvTimne.text = it.timeStart
            } else binding.tvTimne.text = getString(R.string.all_time)
            if (it.title.isNotEmpty()) {
                binding.edtTitleTask.setText(it.title)
            }
            if (it.description.isNotEmpty()) {
                binding.edtDescription.setText(it.description)
            }

            binding.swReminder.isChecked = it.isReminder
            binding.tvCreate.text = getString(R.string.update)
        }

        binding.tvDate.text = viewModel.task.dateStart
    }

    private fun setUpObserver() {
        viewModel.listColorLiveData.observe(viewLifecycleOwner) {
            chooseColorAdapter.submitList(it)
        }

        viewModel.showToastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.statusUpdateTask.observe(viewLifecycleOwner) {
            if (!AdManager.showInter(false, TAG_INTER_ADD_TASK)) dismiss()
        }

        AdManager.nativeAddTaskLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                if (activity == null) return@observe
                binding.layoutAdsAb.visible()
                val frameLayout: FrameLayout = binding.layoutAdsAb
                val adView = LayoutInflater.from(context).inflate(
                    R.layout.native_add_task, null
                ) as NativeAdView
                AdManager.populateUnifiedNativeAdView(it, adView)
                frameLayout.removeAllViews()
                frameLayout.addView(adView)
                didShowNativeAd = true
            }
        }

        if(AdManager.nativeAddTaskLiveData.value == null && !AdManager.isDoingLoadNativeAddTask) {
            AdManager.loadNativeAddTask()
        }
    }

    private fun initRecyclerView() {
        chooseColorAdapter = ChooseColorAdapter()
        chooseColorAdapter.onClickItem = {
            viewModel.task.color = it.color
            binding.container.setBackgroundColor(Color.parseColor(it.color))
            binding.nestedScrollView.setBackgroundColor(Color.parseColor(it.color))
        }
        binding.rvColor.adapter = chooseColorAdapter
        binding.rvColor.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun bindingAction() {
        binding.edtTitleTask.requestFocus()

        binding.container.setSafeOnClickListener {
            hiddenKeyboard()
        }

        binding.btnChooseTime.setSafeOnClickListener {
            showTimePicker()
        }

        binding.btnChooseDate.setSafeOnClickListener {
            showDatePicker()
        }

        binding.edtTitleTask.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // This method is called to notify you that, within 's', the 'count' characters
                // beginning at 'start' are about to be replaced by new text with length 'after'.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // This method is called to notify you that, within 's', the 'count' characters
                // beginning at 'start' have just replaced old text that had length 'before'.
            }

            override fun afterTextChanged(s: Editable) {
                // This method is called to notify you that, somewhere within 's', the text has
                // been changed.
                viewModel.task.title = s.toString()
            }
        })

        binding.edtDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // This method is called to notify you that, within 's', the 'count' characters
                // beginning at 'start' are about to be replaced by new text with length 'after'.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // This method is called to notify you that, within 's', the 'count' characters
                // beginning at 'start' have just replaced old text that had length 'before'.
            }

            override fun afterTextChanged(s: Editable) {
                // This method is called to notify you that, somewhere within 's', the text has
                // been changed.
                viewModel.task.description = s.toString()
            }
        })

        binding.swReminder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.task.isReminder = isChecked
        }

        binding.tvCreate.setSafeOnClickListener {
            if (taskEdit == null) {
                viewModel.addTask()
            } else {
                viewModel.updateTask(viewModel.task)
            }
        }

        binding.ivClose.setSafeOnClickListener {
            if (!AdManager.showInter(false, TAG_INTER_ADD_TASK)) dismiss()
        }
    }

    private fun showDatePicker() {
        val datePickerPopup = DatePickerPopup.Builder()
            .from(context)
            .offset(1)
            .textSize(14)
            .selectedDate(getCurrentTime())
            .darkModeEnabled(false)
            .listener(object : DatePickerPopup.DateSelectListener {


                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDateSelected(
                    dp: com.ozcanalasalvar.datepicker.view.datepicker.DatePicker?,
                    date: Long,
                    day: Int,
                    month: Int,
                    year: Int
                ) {
                    val dateString = AppUtils.formatLongToDateString(date)
                    binding.tvDate.text = dateString
                    viewModel.task.dateStart = dateString
                }
            })
            .build()

        datePickerPopup.show(childFragmentManager, TAG)
    }

    private fun showTimePicker() {
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
                override fun onTimeSelected(
                    timePicker: TimePicker?,
                    hour: Int,
                    minute: Int,
                    format: String?
                ) {
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    binding.tvTimne.text = formattedTime
                    viewModel.task.timeStart = formattedTime
                }
            })
            .build()

        pickerPopup.show(childFragmentManager, TAG)
    }

    @Subscribe
    fun interAdEvent(event: InterAdEvent) {
        if (event.tag == TAG_INTER_ADD_TASK && !event.isShow) {
            dismiss()
        }
    }

    @Subscribe
    fun openAdEvent(event: OpenAdEvent) {
        if (event.isShow) {
            binding.layoutAdsAb.gone()
        } else binding.layoutAdsAb.visible()
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (didShowNativeAd) {
            AdManager.nativeAddTaskLiveData.value = null
            AdManager.loadNativeAddTask()
        }
    }

    companion object {

        private const val KEY_TASK = "task"
        private const val KEY_DATE = "date"
        const val TAG = "AddTaskDialog"

        fun newInstance(task: Task?, dateStart: String = ""): AddTaskDialog {
            val dialog = AddTaskDialog()
            val bundle = Bundle()
            bundle.putString(KEY_TASK, Gson().toJson(task))
            bundle.putString(KEY_DATE, dateStart)
            dialog.arguments = bundle
            return dialog
        }
    }
}