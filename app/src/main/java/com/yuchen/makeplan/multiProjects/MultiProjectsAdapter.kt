package com.yuchen.makeplan.multiProjects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemMultiProjectBinding
import com.yuchen.makeplan.ext.toPx
import com.yuchen.makeplan.task.TaskColorAdapter
import com.yuchen.makeplan.util.TimeUtil.StampToDate
import com.yuchen.makeplan.util.UserManager

class MultiProjectsAdapter : RecyclerView.Adapter<MultiProjectsAdapter.MultiProjectHolder>() {

    private var projectList: List<MultiProject>? =null

    fun submitList(projectList: List<MultiProject>) {
        this.projectList = projectList
        notifyDataSetChanged()
    }

    private var projectClickListener: ProjectClickListener? = null
    fun setProjectClickListener(projectClickListener: ProjectClickListener?) {
        this.projectClickListener = projectClickListener
    }

    interface ProjectClickListener {
        fun onProjectClick(project: MultiProject)
        fun onProjectLongClick(project: MultiProject)
    }

    inner class MultiProjectHolder(var binding: ItemMultiProjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: MultiProject) {
            binding.project = project
            binding.itemMultiProjectCard.setOnClickListener {
                projectClickListener?.onProjectClick(project)
            }
            binding.itemMultiProjectCard.setOnLongClickListener {
                projectClickListener?.onProjectLongClick(project)
                true
            }
            binding.itemMultiProjectEditTime.text = StampToDate(project.updateTime)
            if ( project.receiveUid.size == 0 ){
                binding.itemMultiNotify.visibility = View.INVISIBLE
            }else{
                binding.itemMultiNotify.visibility = View.VISIBLE
                if (project.receiveUid.size <=999)
                    binding.itemMultiNotify.text = project.receiveUid.size.toString()
                else
                    binding.itemMultiNotify.text = "999+"
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiProjectHolder {
        return MultiProjectHolder(ItemMultiProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MultiProjectHolder, position: Int) {
        projectList?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return projectList?.let {it.size} ?: 0
    }
}