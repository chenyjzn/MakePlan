package com.yuchen.makeplan.gantt

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.ItemGanttTaskBinding
import com.yuchen.makeplan.util.TimeUtil.StampToDate

class GanttTaskAdapter() : RecyclerView.Adapter<GanttTaskAdapter.GanttTaskHolder>() {

    private var project: Project? = null

    class GanttTaskHolder(var binding: ItemGanttTaskBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project,pos: Int) {
            binding.task = project.taskList[pos]
            binding.ganttTaskTimeLine.setRange(project.startTimeMillis,project.endTimeMillis)
            binding.ganttTaskBar.setRange(project.startTimeMillis,project.endTimeMillis,project.taskList[pos].startTimeMillis,project.taskList[pos].endTimeMillis)
            binding.ganttTaskBar.taskName = project.taskList[pos].name + StampToDate(project.taskList[pos].startTimeMillis) + StampToDate(project.taskList[pos].endTimeMillis) + project.taskList[pos].completeRate.toString()
            binding.ganttTaskBar.colorString = project.taskList[pos].color
            binding.ganttText.setPosProportion(project.startTimeMillis,project.endTimeMillis,project.taskList[pos].startTimeMillis)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GanttTaskHolder {
        return GanttTaskHolder(ItemGanttTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: GanttTaskHolder, position: Int) {
        project?.let {
            holder.bind(it,position)
        }
    }

    override fun getItemCount(): Int {
        return project?.let {it.taskList.size}?:0
    }

    fun submitProject(project: Project) {
        this.project = project
        notifyDataSetChanged()
    }
}