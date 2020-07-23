package com.yuchen.makeplan.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.databinding.ItemTaskColorBinding

class TaskColorAdapter(private val viewModel : TaskViewModel) : RecyclerView.Adapter<TaskColorAdapter.TaskColorHolder>() {

    inner class TaskColorHolder(var binding: ItemTaskColorBinding) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(viewModel: TaskViewModel, position: Int) {
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            binding.colorString = viewModel.colorList[position]
            binding.taskColor.setOnClickListener {
                viewModel.newTaskColorPair.value = position to viewModel.colorList[position]
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskColorHolder {
        return TaskColorHolder(ItemTaskColorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TaskColorHolder, position: Int) {
        holder.bind(viewModel,position)
    }

    override fun getItemCount(): Int {
        return viewModel.colorList.size
    }

    override fun onViewAttachedToWindow(holder: TaskColorHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: TaskColorHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }

}