package com.yuchen.makeplan.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.ItemProjectBinding
import com.yuchen.makeplan.projects.AvatarAdapter
import com.yuchen.makeplan.projects.ProjectsViewModel
import com.yuchen.makeplan.util.TimeUtil.StampToDate

class SearchAdapter(private val viewModel : ProjectsViewModel) : RecyclerView.Adapter<SearchAdapter.ProjectHolder>() {

    private var projectList: List<Project>? = null

    class ProjectHolder(var binding: ItemProjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project,viewModel : ProjectsViewModel) {
            binding.project = project

            val adapter = AvatarAdapter()
            binding.itemProjectAvatarRecycler.adapter = adapter
            adapter.submitMembers(project.members)

            binding.itemProjectAvatarRecycler.layoutManager = LinearLayoutManager(binding.itemProjectAvatarRecycler.context,LinearLayoutManager.HORIZONTAL,false)

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