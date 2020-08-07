package com.yuchen.makeplan.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.ItemProjectBinding
import com.yuchen.makeplan.util.TimeUtil.millisToDate

class ProjectsAdapter(val viewModel: ProjectsViewModel) : RecyclerView.Adapter<ProjectsAdapter.ProjectHolder>() {

    private var projectList: List<Project>? = null

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

    inner class ProjectHolder(var binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(project: Project) {
            binding.project = project
            viewModel.loadingStatus.observe(this, Observer {
                it?.let {
                    when (it) {
                        is LoadingStatus.LOADING -> {
                            binding.itemProjectCard.isEnabled = false
                        }
                        is LoadingStatus.DONE -> {
                            binding.itemProjectCard.isEnabled = true
                        }
                        is LoadingStatus.ERROR -> {

                        }
                    }
                }
            })

            viewModel.isFABCooling.observe(this, Observer {
                it?.let {
                    binding.itemProjectCard.isEnabled = !it
                }
            })

            binding.itemProjectCard.setOnClickListener {
                onClickListener?.onProjectClick(project)
            }
            binding.itemProjectCard.setOnLongClickListener {
                onClickListener?.onProjectLongClick(project)
                true
            }
            binding.itemProjectEditTime.text = millisToDate(project.updateTime)
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
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
        return projectList?.size ?: 0
    }

    override fun onViewAttachedToWindow(holder: ProjectHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: ProjectHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }
}