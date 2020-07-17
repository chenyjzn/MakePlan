package com.yuchen.makeplan.projects

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.ItemProjectBinding
import com.yuchen.makeplan.util.TimeUtil.StampToDate

class ProjectsAdapter(private val viewModel : ProjectsViewModel) : RecyclerView.Adapter<ProjectsAdapter.ProjectHolder>() {

    private var projectList: List<Project>? = null

    class ProjectHolder(var binding: ItemProjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project,viewModel : ProjectsViewModel) {
            binding.project = project

            val adapter = AvatarAdapter()
            binding.itemProjectAvatarRecycler.adapter = adapter
            adapter.submitMembers(project.members)

            binding.itemProjectAvatarRecycler.layoutManager = LinearLayoutManager(binding.itemProjectAvatarRecycler.context,LinearLayoutManager.HORIZONTAL,false)

            binding.itemProjectAvatarRecycler.setOnClickListener {
                viewModel.goToGantt(project)
            }

            binding.itemProjectCard.setOnClickListener {
                viewModel.goToGantt(project)
            }
            binding.itemProjectCard.setOnLongClickListener {
                viewModel.goToProjectSetting(project)
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
            holder.bind(it[position],viewModel)
        }
    }

    override fun getItemCount(): Int {
        return projectList?.let {it.size} ?: 0
    }

    fun submitProjects(projectList: List<Project>) {
        this.projectList = projectList
        notifyDataSetChanged()
    }
}