package com.example.dailytaskplanner.ui.dialog

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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.adapter.ChooseColorAdapter
import com.example.dailytaskplanner.databinding.DialogAddTaskBinding
import com.example.dailytaskplanner.model.Task
import com.example.dailytaskplanner.utils.setSafeOnClickListener
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.ozcanalasalvar.datepicker.utils.DateUtils.getCurrentTime
import com.ozcanalasalvar.datepicker.view.popup.DatePickerPopup
import com.ozcanalasalvar.datepicker.view.popup.TimePickerPopup
import com.ozcanalasalvar.datepicker.view.timepicker.TimePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AddTaskDialog : DialogFragment() {

    lateinit var binding: DialogAddTaskBinding

    private val viewModel: AddTaskViewModel by viewModels()

    private lateinit var chooseColorAdapter: ChooseColorAdapter

    private var taskEdit: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val task = Gson().fromJson(it.getString(KEY_TASK), Task::class.java)
            task?.let {
                taskEdit = it
                viewModel.task = it
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
    }

    private fun initData() {
        taskEdit?.let {
            if (it.color.isNotEmpty()) {
                binding.container.setBackgroundColor(Color.parseColor(it.color))
            }
            if (it.dateStart.isNotEmpty()) {
                binding.tvDate.text = it.dateStart
            }
            if (it.timeStart.isNotEmpty()) {
                binding.tvTimne.text = it.timeStart
            }
            if (it.title.isNotEmpty()) {
                binding.edtTitleTask.setText(it.title)
            }
            if (it.description.isNotEmpty()) {
                binding.edtDescription.setText(it.description)
            }

            binding.swReminder.isChecked = it.isReminder
            binding.tvCreate.text = getString(R.string.update)
        }
    }

    private fun setUpObserver() {
        viewModel.listColorLiveData.observe(viewLifecycleOwner) {
            chooseColorAdapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        chooseColorAdapter = ChooseColorAdapter()
        chooseColorAdapter.onClickItem = {
            viewModel.task.color = it.color
            binding.container.setBackgroundColor(Color.parseColor(it.color))
        }
        binding.rvColor.adapter = chooseColorAdapter
        binding.rvColor.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun bindingAction() {
        binding.ivTime.setSafeOnClickListener {
            showTimePicker()
        }

        binding.ivDate.setSafeOnClickListener {
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
            dismiss()
        }

        binding.ivClose.setSafeOnClickListener {
            dismiss()
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
                    binding.tvDate.text = "$day/$month/$year"
                    viewModel.task.dateStart = "$day/$month/$year"
                    Toast.makeText(context, "$day/$month/$year", Toast.LENGTH_SHORT).show()
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

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    companion object {

        private const val KEY_TASK = "task"
        const val TAG = "AddTaskDialog"

        fun newInstance(task: Task?): AddTaskDialog {
            val dialog = AddTaskDialog()
            val bundle = Bundle()
            bundle.putString(KEY_TASK, Gson().toJson(task))
            dialog.arguments = bundle
            return dialog
        }
    }
}