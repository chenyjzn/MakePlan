package com.yuchen.makeplan.multigantt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.data.MultiTask
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.databinding.FragmentMultiGanttBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.view.MultiGanttChart
import kotlin.math.roundToInt

class MultiGanttFragment : Fragment() {

    private val viewModel: MultiGanttViewModel by viewModels<MultiGanttViewModel> { getVmFactory(MultiGanttFragmentArgs.fromBundle(requireArguments()).multiProject)}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMultiGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.project.observe(viewLifecycleOwner, Observer {
            it?.let {
//                binding.multiGanttTimeLine.setRange(it.startTimeMillis,it.endTimeMillis)
//                binding.multiGanttTimeLine.invalidate()
//                binding.multiGanttChart.setRange(it.startTimeMillis,it.endTimeMillis)
//                binding.multiGanttChart.invalidate()
            }
        })

        binding.multiGanttAddTask.setOnClickListener {
            if (viewModel.taskSelect.value == null && viewModel.project.value!= null)
                this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToMultiTaskFragment(viewModel.project.value!!,null))
        }

        binding.multiGanttEdit.setOnClickListener {
            if (viewModel.taskSelect.value != null && viewModel.project.value!= null){
                this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToMultiTaskFragment(viewModel.project.value!!,viewModel.taskSelect.value!!))
                viewModel.taskSelectClear()
            }
        }

        binding.multiGanttDelete.setOnClickListener {
            if (viewModel.taskSelect.value != null)
                viewModel.taskRemove()
        }

        return binding.root
    }

}
