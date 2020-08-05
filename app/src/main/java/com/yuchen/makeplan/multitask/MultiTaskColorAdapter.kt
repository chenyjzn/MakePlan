package com.yuchen.makeplan.multitask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.databinding.ItemMultiTaskColorBinding

class MultiTaskColorAdapter(private val viewModel: MultiTaskViewModel) : RecyclerView.Adapter<MultiTaskColorAdapter.TaskColorHolder>() {

    class TaskColorHolder(var binding: ItemMultiTaskColorBinding) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(viewModel: MultiTaskViewModel, position: Int) {
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            binding.colorString = viewModel.colorList[position]
            binding.multiTaskColor.setOnClickListener {
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
        return TaskColorHolder(ItemMultiTaskColorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TaskColorHolder, position: Int) {
        holder.bind(viewModel, position)
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