package com.yuchen.makeplan.multi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemMultiProjectBinding
import com.yuchen.makeplan.util.TimeUtil.StampToDate

class MultiItemsAdapter() : RecyclerView.Adapter<MultiItemsAdapter.MultiProjectHolder>() {

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