package com.example.dailytaskplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytaskplanner.databinding.ItemColorBinding
import com.example.dailytaskplanner.model.ColorTask

class ChooseColorAdapter :
    ListAdapter<ColorTask, ChooseColorAdapter.ColorViewHolder>(ColorDiffCallback()) {

    var onClickItem: ((ColorTask) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            ItemColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class ColorViewHolder(private val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {
                onClickItem?.invoke(getItem(adapterPosition))
            }

        }

        fun bindData(colorTask: ColorTask) {
            binding.viewColor.setBackgroundColor(Color.parseColor(colorTask.color))
        }
    }

    class ColorDiffCallback : DiffUtil.ItemCallback<ColorTask>() {
        override fun areItemsTheSame(oldItem: ColorTask, newItem: ColorTask): Boolean {
            return oldItem.color == newItem.color
        }

        override fun areContentsTheSame(oldItem: ColorTask, newItem: ColorTask): Boolean {
            return oldItem == newItem
        }
    }
}