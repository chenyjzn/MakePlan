package com.yuchen.makeplan.multitask

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.ItemMultiTaskColorBinding
import com.yuchen.makeplan.databinding.ItemTaskColorBinding

class MultiTaskColorAdapter(private val viewModel : MultiTaskViewModel) : RecyclerView.Adapter<MultiTaskColorAdapter.TaskColorHolder>() {

    private var colorList: List<String> = viewModel.colorList

    class TaskColorHolder(var binding: ItemMultiTaskColorBinding) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(viewModel: MultiTaskViewModel, colorString : String) {
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            binding.colorString = colorString
            binding.multiTaskColor.setOnClickListener {
                viewModel.newTaskColor.value = colorString
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
        holder.bind(viewModel,colorList[position])
    }

    override fun getItemCount(): Int {
        return colorList.size
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