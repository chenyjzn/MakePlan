package com.yuchen.makeplan.gantt

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.ItemGanttTaskBinding
import com.yuchen.makeplan.util.TimeUtil.StampToDate
import com.yuchen.makeplan.view.GanttTaskBar

//class GanttTaskAdapter() : RecyclerView.Adapter<GanttTaskAdapter.GanttTaskHolder>() {
//
//    private var project: Project? = null
//
//    class GanttTaskHolder(var binding: ItemGanttTaskBinding): RecyclerView.ViewHolder(binding.root) {
//
//        var eventX : Float = 0f
//        var eventY : Float = 0f
//
//        fun bind(project: Project,pos: Int) {
//            val task = project.taskList[pos]
//            binding.task = task
//            binding.ganttTaskTimeLine.setRange(project.startTimeMillis,project.endTimeMillis)
//            binding.ganttTaskBar.taskBarInit(project.startTimeMillis,project.endTimeMillis,task.startTimeMillis,task.endTimeMillis,task.completeRate,task.color)
//            binding.ganttText.setPosProportion(project.startTimeMillis,project.endTimeMillis,project.taskList[pos].startTimeMillis)
//            binding.ganttTaskBar.setOnTouchListener { v, event ->
//                eventX = event.x
//                eventY = event.y
//                false
//            }
//            binding.ganttTaskBar.setOnClickListener {
//                it?.let {
//                    if ((it as GanttTaskBar).isClick(eventX,eventY))
//                        Log.d("chenyjzn","Task click")
//                    else
//                        Log.d("chenyjzn","Task not click")
//                }
//            }
//            binding.executePendingBindings()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GanttTaskHolder {
//        return GanttTaskHolder(ItemGanttTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//    }
//
//    override fun onBindViewHolder(holder: GanttTaskHolder, position: Int) {
//        project?.let {
//            holder.bind(it,position)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return project?.let {it.taskList.size}?:0
//    }
//
//    fun submitProject(project: Project) {
//        this.project = project
//        notifyDataSetChanged()
//    }
//}