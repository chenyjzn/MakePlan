package com.yuchen.makeplan.projects

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.ItemProjectAddBinding
import com.yuchen.makeplan.databinding.ItemProjectBinding
import com.yuchen.makeplan.util.TimeUtil.StampToDate

class ProjectsAdapter(private val viewModel : ProjectsViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var projectList: List<Project>? = null

    class ProjectHolder(var binding: ItemProjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project,viewModel : ProjectsViewModel) {
            binding.project = project
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

    class ProjectAddHolder(var binding: ItemProjectAddBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel : ProjectsViewModel) {
            binding.itemProjectAddCard.setOnClickListener {

            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProjectHolder(ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//        return when(viewType){
//            PROJECT -> {
//                ProjectHolder(ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//            }
//            ADD_PROJECT -> {
//                ProjectAddHolder(ItemProjectAddBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//            }
//            else -> {
//                throw ClassCastException("Unknown viewType $viewType")
//            }
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ProjectHolder -> {
                projectList?.let {
                    holder.bind(it[position],viewModel)
                }
            }
            is ProjectAddHolder -> {
                holder.bind(viewModel)
            }
        }
    }

//    override fun getItemViewType(position: Int): Int {
//        if (position < projectList?.size ?: -1){
//            return PROJECT
//        }
//        return ADD_PROJECT
//    }

    override fun getItemCount(): Int {
//        return projectList?.let {it.size+1}?:1
        return projectList?.let {it.size} ?: 0
    }

    fun submitProjects(projectList: List<Project>) {
        this.projectList = projectList
        notifyDataSetChanged()
    }

    companion object{
        const val PROJECT = 0
        const val ADD_PROJECT = 1
    }
}