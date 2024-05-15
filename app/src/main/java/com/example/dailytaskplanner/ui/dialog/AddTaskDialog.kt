package com.example.dailytaskplanner.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.adapter.ChooseColorAdapter
import com.example.dailytaskplanner.databinding.DialogAddTaskBinding
import com.example.dailytaskplanner.model.SubTask
import com.example.dailytaskplanner.model.Task
import com.example.dailytaskplanner.utils.setSafeOnClickListener
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class AddTaskDialog : DialogFragment() {

    lateinit var binding: DialogAddTaskBinding

    private val viewModel: AddTaskViewModel by viewModels()

    lateinit var chooseColorAdapter: ChooseColorAdapter

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
        initRecyclerView()
        bindingAction()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.listColorLiveData.observe(viewLifecycleOwner) {
            chooseColorAdapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        chooseColorAdapter = ChooseColorAdapter()
        chooseColorAdapter.onClickItem = {
            viewModel.colorSelected = it.color
            binding.container.setBackgroundColor(Color.parseColor(it.color))
        }
        binding.rvColor.adapter = chooseColorAdapter
        binding.rvColor.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun bindingAction() {
        binding.tvCreate.setSafeOnClickListener {
            val task = Task(
                id = System.currentTimeMillis(),
                icon = R.drawable.icon_task,
                color = viewModel.colorSelected,
                title = binding.edtTitleTask.text.toString().trim(),
                description = "Task Description",
                category = "Task Category",
                dateStart = "2022-01-01",
                timeStart = "12:00",
                isCompleted = false,
                isReminder = false,
                timeReminder = "12:00:00 AM 25/05/2025",
                subTasks = Gson().toJson(
                    listOf(
                        SubTask(
                            id = Random.nextLong(),
                            title = "SubTask 1",
                            isCompleted = false
                        ),
                        SubTask(
                            id = Random.nextLong(),
                            title = "SubTask 2",
                            isCompleted = false
                        )
                    )
                )
            )
            viewModel.addTask(task)
            dismiss()
        }

        binding.ivClose.setSafeOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    companion object {
        const val TAG = "AddTaskDialog"

        fun newInstance() = AddTaskDialog()
    }
}