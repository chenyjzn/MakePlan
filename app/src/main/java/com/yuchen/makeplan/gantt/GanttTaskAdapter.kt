package com.yuchen.makeplan.gantt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.databinding.ItemGanttTaskBinding

class GanttTaskAdapter(viewModel:GanttViewModel) : ListAdapter<Task, GanttTaskAdapter.GanttTaskHolder>(DiffCallback) {

    var projectStartTimeMillis = 0L
    var projectEndTimeMillis = 0L

    class GanttTaskHolder(var binding: ItemGanttTaskBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.task = task
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GanttTaskHolder {
        return GanttTaskHolder(ItemGanttTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: GanttTaskHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
        if (projectStartTimeMillis!=projectEndTimeMillis){
            holder.binding.ganttTaskTimeLine.setRange(projectStartTimeMillis,projectEndTimeMillis)
            holder.binding.ganttTaskTimeLine.invalidate()
            holder.binding.ganttTaskBar.setRange(projectStartTimeMillis,projectEndTimeMillis,task.startTimeMillis,task.endTimeMillis)
            holder.binding.ganttTaskBar.taskName = task.name
            holder.binding.ganttTaskBar.invalidate()
        }
    }
}