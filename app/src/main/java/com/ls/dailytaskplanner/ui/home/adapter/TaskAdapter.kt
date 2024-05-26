package com.ls.dailytaskplanner.ui.home.adapter

import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ls.dailytaskplanner.App
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.databinding.ItemTaskBinding
import com.ls.dailytaskplanner.model.Task
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.AppUtils.toTimeString
import com.ls.dailytaskplanner.utils.MediaPlayerManager
import com.ls.dailytaskplanner.utils.TrackingHelper
import com.ls.dailytaskplanner.utils.setSafeOnClickListener

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    var onClickItem: ((Task) -> Unit)? = null
    var onClickCheckbox: ((Task) -> Unit)? = null
    private var mediaPlayer = MediaPlayer.create(App.mInstance.applicationContext, R.raw.done)

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

            binding.root.setSafeOnClickListener {
                onClickItem?.invoke(getItem(adapterPosition))
            }

            binding.cbDone.setSafeOnClickListener {
                TrackingHelper.logEvent(AllEvents.ACTION_CHANGE_STATUS_TASK)
                if (binding.cbDone.isChecked) {
                    mediaPlayer.start()
                    MediaPlayerManager.playRawFile(R.raw.done)
                }
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