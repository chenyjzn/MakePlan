package com.yuchen.makeplan.gantt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.databinding.FragmentGanttBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.view.GanttChart
import com.yuchen.makeplan.view.GanttChartGroup
import java.util.*
import kotlin.math.hypot
import kotlin.math.pow


class GanttFragment : Fragment() {

    private val viewModel: GanttViewModel by viewModels<GanttViewModel> { getVmFactory(GanttFragmentArgs.fromBundle(requireArguments()).projectHistory)}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.ganttChartGroup.setColorList(resources.getStringArray(R.array.color_array_1).toList(),resources.getStringArray(R.array.color_array_2).toList())

        viewModel.project.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.ganttChartGroup.setRange(it.startTimeMillis,it.endTimeMillis)
                binding.ganttChartGroup.setTaskList(it.taskList)
                binding.ganttChartGroup.invalidate()
            }
        })

        binding.ganttChartGroup.setOnEventListener(object : GanttChartGroup.OnEventListener{
            override fun eventChartTime(startTimeMillis: Long, endTimeMillis: Long) {
                viewModel.setProjectTime(startTimeMillis,endTimeMillis)
            }

            override fun eventMoveDx(dx: Float, width: Int) {
                TODO("Not yet implemented")
            }

            override fun eventZoomDlDr(dl: Float, dr: Float, width: Int) {
                TODO("Not yet implemented")
            }

            override fun eventTaskSelect(taskPos: Int, taskValue: Task?) {
                Log.d("chenyjzn","Task $taskPos")
                viewModel.setTaskSelect(taskPos)
            }
        })

        viewModel.navigateToTaskSetting.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(GanttFragmentDirections.actionGanttFragmentToTaskFragment(viewModel.getUndoListArray(),it))
                viewModel.goToTaskDone()
            }
        })

        viewModel.projectSaveSuccess.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    this.findNavController().popBackStack()
                    viewModel.saveProjectDone()
                }
            }
        })

        viewModel.taskSelect.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.ganttChartGroup.setTaskSelect(it)
                binding.ganttChartGroup.invalidate()
            }
        })

        binding.ganttAddTask.setOnClickListener {
            if (viewModel.isTaskSelect() == -1)
                viewModel.goToAddTask()
        }

        binding.ganttUndo.setOnClickListener {
            if (viewModel.isTaskSelect() == -1)
                viewModel.unDoAction()
        }

        binding.ganttRedo.setOnClickListener {
            if (viewModel.isTaskSelect() == -1)
                viewModel.reDoAction()
        }

        binding.ganttSave.setOnClickListener {
            if (viewModel.isTaskSelect() == -1)
                viewModel.saveProject()
        }

        binding.ganttEdit.setOnClickListener {
            if (viewModel.isTaskSelect() > -1){
                viewModel.goToEditTask(viewModel.isTaskSelect())
            }
        }

        binding.ganttDelete.setOnClickListener {
            if (viewModel.isTaskSelect() > -1){
                viewModel.taskRemove(viewModel.isTaskSelect())
            }
        }

        return binding.root
    }
}