package com.example.dailytaskplanner.ui.home

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.base.BaseFragment
import com.example.dailytaskplanner.databinding.FragHomeBinding
import com.example.dailytaskplanner.ui.dialog.AddTaskDialog
import com.example.dailytaskplanner.ui.home.adapter.TaskAdapter
import com.example.dailytaskplanner.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragHomeBinding, HomeViewModel>() {

    private val viewModel: HomeViewModel by viewModels()
    lateinit var adapterTask: TaskAdapter

    override fun getLayoutId() = R.layout.frag_home

    override fun viewCreated() {
        initRvTask()
        viewModel.getAllTasks()
    }

    override fun observersSomething() {
        viewModel.listTaskLiveData.observe(viewLifecycleOwner) {
            adapterTask.submitList(it)
        }
    }

    override fun bindingAction() {
        binding.btnAdd.setSafeOnClickListener {
            AddTaskDialog.newInstance(null).show(childFragmentManager, AddTaskDialog.TAG)
        }
    }

    private fun initRvTask() {
        adapterTask = TaskAdapter()
        adapterTask.onClickItem = {
            AddTaskDialog.newInstance(it).show(childFragmentManager, AddTaskDialog.TAG)
        }
        adapterTask.onClickCheckbox = {
            it.isCompleted = !it.isCompleted
            viewModel.updateTask(it)
        }
        binding.rvTask.adapter = adapterTask
        binding.rvTask.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
    }

}