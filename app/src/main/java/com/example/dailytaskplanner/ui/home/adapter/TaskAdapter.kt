package com.example.dailytaskplanner.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytaskplanner.App
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.databinding.ItemTaskBinding
import com.example.dailytaskplanner.model.Task
import com.example.dailytaskplanner.utils.AppUtils.toTimeString
import com.example.dailytaskplanner.utils.setSafeOnClickListener

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    var onClickItem: ((Task) -> Unit)? = null
    var onClickCheckbox: ((Task) -> Unit)? = null


    fun removeItem(position: Int) {
        val task = getItem(position)
        val list = currentList.toMutableList()
        list.remove(task)
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {
                onClickItem?.invoke(getItem(adapterPosition))
            }

            binding.cbDone.setSafeOnClickListener {
                onClickCheckbox?.invoke(getItem(adapterPosition))
            }

        }

        fun bindData(task: Task) {
            binding.icon.setImageResource(task.icon)
            if(task.color.isNotEmpty()) {
                binding.container.setCardBackgroundColor(Color.parseColor(task.color))
            } else {
                binding.container.setCardBackgroundColor(Color.parseColor("#99FFFF"))
            }
            binding.tvTitle.text = task.title
            binding.tvTimeStart.text =
                if (task.timeStart == "00:00") App.mInstance.getString(R.string.all_time) else task.timeStart.toTimeString()
            binding.cbDone.isChecked = task.isCompleted
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}