package com.yuchen.makeplan.gantt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.ItemGanttToolBinding

class GanttToolBarAdapter(val viewModel : GanttViewModel) : RecyclerView.Adapter<GanttToolBarAdapter.GanttToolHolder>() {

    private var toolType = ToolType.GNATT_TOOL

    class GanttToolHolder(var binding: ItemGanttToolBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(toolType : ToolType,pos : Int,viewModel : GanttViewModel) {
            if (toolType == ToolType.GNATT_TOOL){
                when(pos){
                    ADD_TASK -> {
                        binding.ganttToolIcon.setImageResource(R.drawable.ic_add_circle_black_24dp)
                        binding.ganttToolIcon.setOnClickListener {
                            viewModel.goToAddTask()
                        }
                    }
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GanttToolHolder {
        return GanttToolHolder(ItemGanttToolBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: GanttToolHolder, position: Int) {
        holder.bind(toolType,position,viewModel)
    }

    override fun getItemCount(): Int {
        return toolType.toolNum
    }

    fun setTool(isGanttTool : Boolean){
        if (isGanttTool)
            toolType = ToolType.GNATT_TOOL
        else
            toolType = ToolType.TASK_TOOL
        notifyDataSetChanged()
    }

    companion object{
        const val ADD_TASK = 0
        const val PROJECT_NAME = 1

        const val EDIT_TASK = 0

    }
}

enum class ToolType(val toolNum : Int){
    GNATT_TOOL(1),
    TASK_TOOL(1)
}