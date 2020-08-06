package com.yuchen.makeplan.notify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemMultiProjectBinding
import com.yuchen.makeplan.util.TimeUtil.stampToDate

class NotifyItemsAdapter(private val viewModel: NotifyItemsViewModel) : RecyclerView.Adapter<NotifyItemsAdapter.MultiProjectHolder>() {

    private var projectList: List<MultiProject>? = null

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

    inner class MultiProjectHolder(var binding: ItemMultiProjectBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(project: MultiProject) {
            binding.project = project
            viewModel.loadingStatus.observe(this, Observer {
                it?.let {
                    when (it) {
                        is LoadingStatus.LOADING -> {
                            binding.itemMultiProjectCard.isEnabled = false
                        }
                        is LoadingStatus.DONE -> {
                            binding.itemMultiProjectCard.isEnabled = true
                        }
                        is LoadingStatus.ERROR -> {

                        }
                    }
                }
            })
            binding.itemMultiProjectCard.setOnClickListener {
                projectClickListener?.onProjectClick(project)
            }
            binding.itemMultiProjectCard.setOnLongClickListener {
                projectClickListener?.onProjectLongClick(project)
                true
            }
            binding.itemMultiProjectEditTime.text = stampToDate(project.updateTime)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiProjectHolder {
        return MultiProjectHolder(
            ItemMultiProjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MultiProjectHolder, position: Int) {
        projectList?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return projectList?.let { it.size } ?: 0
    }

    override fun onViewAttachedToWindow(holder: MultiProjectHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: MultiProjectHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }
}