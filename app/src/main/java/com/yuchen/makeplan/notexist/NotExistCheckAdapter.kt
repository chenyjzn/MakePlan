package com.yuchen.makeplan.notexist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.ItemNotExistProjectBinding

class NotExistCheckAdapter(private val viewModel: NotExistCheckViewModel) : RecyclerView.Adapter<NotExistCheckAdapter.NotExistCheckHolder>() {

    private var notExistProject : List<Project>? = null
    var checkList : MutableList<Boolean> = mutableListOf()

    class NotExistCheckHolder(var binding: ItemNotExistProjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.project = project
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotExistCheckHolder {
        return NotExistCheckHolder(ItemNotExistProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotExistCheckHolder, position: Int) {
        notExistProject?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return notExistProject?.size ?: 0
    }

    fun submitProject(notExistProject: List<Project>) {
        this.notExistProject = notExistProject
        for (i in 0..notExistProject.size){
            checkList.add(false)
        }
        notifyDataSetChanged()
    }
}