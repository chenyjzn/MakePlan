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

    private var onClickListener: OnClickListener? = null
    fun setItemClickListener(itemClickListener: OnClickListener?) {
        onClickListener = itemClickListener
    }

    interface OnClickListener {
        fun onProjectClick(project: MultiProject)
        fun onProjectLongClick(project: MultiProject)
    }

    inner class MultiProjectHolder(var binding: ItemMultiProjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: MultiProject) {
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