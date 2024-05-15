package com.example.dailytaskplanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.databinding.FragHomeBinding
import com.example.dailytaskplanner.model.SubTask
import com.example.dailytaskplanner.model.Task
import com.example.dailytaskplanner.ui.home.adapter.TaskAdapter
import com.example.dailytaskplanner.utils.setSafeOnClickListener
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragHomeBinding.bind(inflater.inflate(R.layout.frag_home, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRvTask()
        bindingAction()
        setUpObserver()
        viewModel.getAllTasks()
    }

    private fun setUpObserver() {
        viewModel.listTaskLiveData.observe(viewLifecycleOwner) {
            (binding.rvTask.adapter as TaskAdapter).setData(it)
        }
    }

    private fun initRvTask() {
        val adapter = TaskAdapter()
        adapter.onClickItem = {
            Toast.makeText(context, it.title, Toast.LENGTH_SHORT).show()
        }
        adapter.onClickCheckbox = {
            it.isCompleted = !it.isCompleted
            viewModel.updateTask(it)
        }
        binding.rvTask.adapter = adapter
        binding.rvTask.layoutManager = LinearLayoutManager(context)
    }

    private fun bindingAction() {
        binding.btnAdd.setSafeOnClickListener {
            // Tạo một hoặc nhiều đối tượng SubTask
            val task = Task(
                id = System.currentTimeMillis(),
                icon = R.drawable.ic_launcher_background,
                color = "#03A9F4",
                title = "Task Title",
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
        }
    }
}