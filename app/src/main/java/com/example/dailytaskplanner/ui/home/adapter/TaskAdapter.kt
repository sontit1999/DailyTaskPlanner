package com.example.dailytaskplanner.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytaskplanner.databinding.ItemTaskBinding
import com.example.dailytaskplanner.model.Task
import com.example.dailytaskplanner.utils.AppUtils
import com.example.dailytaskplanner.utils.setSafeOnClickListener

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val listTask = mutableListOf<Task>()
    var onClickItem: ((Task) -> Unit)? = null
    var onClickCheckbox: ((Task) -> Unit)? = null

    fun setData(listTask: List<Task>) {
        this.listTask.clear()
        this.listTask.addAll(listTask)
        notifyDataSetChanged()
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

    override fun getItemCount() = listTask.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindData(listTask[position])
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {
                onClickItem?.invoke(listTask[adapterPosition])
            }

            binding.cbDone.setSafeOnClickListener {
                onClickCheckbox?.invoke(listTask[adapterPosition])
            }

        }

        fun bindData(task: Task) {
            binding.container.setCardBackgroundColor(Color.parseColor(AppUtils.randomColor()))
            binding.tvTitle.text = task.title
            binding.cbDone.isChecked = task.isCompleted
        }
    }
}