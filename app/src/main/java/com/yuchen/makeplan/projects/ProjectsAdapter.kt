package com.yuchen.makeplan.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.ItemProjectBinding
import com.yuchen.makeplan.util.TimeUtil.StampToDate

class ProjectsAdapter : RecyclerView.Adapter<ProjectsAdapter.ProjectHolder>(){

    private var projectList: List<Project>? =null

    fun submitList(projectList: List<Project>) {
        this.projectList = projectList
    }

    private var onClickListener: OnClickListener? = null
    fun setItemClickListener(itemClickListener: OnClickListener?) {
        onClickListener = itemClickListener
    }

    interface OnClickListener {
        fun onProjectClick(project: Project)
        fun onProjectLongClick(project: Project)
    }

    inner class ProjectHolder(var binding: ItemProjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.project = project
            binding.itemProjectCard.setOnClickListener {
                onClickListener?.onProjectClick(project)
            }
            binding.itemProjectCard.setOnLongClickListener {
                onClickListener?.onProjectLongClick(project)
                true
            }
            binding.itemProjectEditTime.text = StampToDate(project.updateTime)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHolder {
        return ProjectHolder(ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) {
        projectList?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return projectList?.let {it.size} ?: 0
    }
}