package com.yuchen.makeplan.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.ToDo
import com.yuchen.makeplan.databinding.ItemTaskHeaderBinding
import com.yuchen.makeplan.databinding.ItemTaskTodoAddBinding
import com.yuchen.makeplan.databinding.ItemTaskTodoListBinding

class TaskAdapter(private val viewModel : TaskViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var toDoList: List<ToDo>? = null

    class TaskHeaderHolder(var binding: ItemTaskHeaderBinding): RecyclerView.ViewHolder(binding.root),LifecycleOwner {
        fun bind(viewModel : TaskViewModel) {
            binding.lifecycleOwner = this
            val colorAdapter = TaskColorAdapter(viewModel)
            binding.taskColorRecycler.adapter = colorAdapter
            binding.taskColorRecycler.layoutManager = GridLayoutManager(binding.taskColorRecycler.context,6)
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

    class TaskToDoHolder(var binding: ItemTaskTodoListBinding): RecyclerView.ViewHolder(binding.root),LifecycleOwner {
        fun bind(viewModel : TaskViewModel) {
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

    class TaskToDoAddHolder(var binding: ItemTaskTodoAddBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {
        fun bind(viewModel : TaskViewModel) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TASK_HEADER -> {
                TaskHeaderHolder(ItemTaskHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            TODO_LIST -> {
                TaskToDoHolder(ItemTaskTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            TODO_ADD -> {
                TaskToDoAddHolder(ItemTaskTodoAddBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else -> {
                throw ClassCastException("Unknown viewType $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TaskHeaderHolder -> {
                holder.bind(viewModel)
            }
            is TaskToDoHolder -> {
                holder.bind(viewModel)
            }
            is TaskToDoAddHolder -> {
                holder.bind(viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0){
            return TASK_HEADER
        }else if(position == itemCount -1){
            return TODO_ADD
        }else{
            return TODO_LIST
        }
    }

    override fun getItemCount(): Int {
        return toDoList?.let {it.size+2}?:2
    }

    fun submitToDoList(toDoList: List<ToDo>) {
        this.toDoList = toDoList
        notifyDataSetChanged()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when(holder) {
            is TaskHeaderHolder -> {
                holder.onAttach()
            }
            is TaskToDoHolder -> {
                holder.onAttach()
            }
            is TaskToDoAddHolder -> {
                holder.onAttach()
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when(holder) {
            is TaskHeaderHolder -> {
                holder.onDetach()
            }
            is TaskToDoHolder -> {
                holder.onDetach()
            }
            is TaskToDoAddHolder -> {
                holder.onDetach()
            }
        }
    }

    companion object{
        const val TASK_HEADER = 0
        const val TODO_LIST = 1
        const val TODO_ADD = 2
    }
}