package com.yuchen.makeplan.gantt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.databinding.FragmentGanttBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.view.GanttChartGroup


class GanttFragment : Fragment() {

    private val viewModel: GanttViewModel by viewModels<GanttViewModel> { getVmFactory(GanttFragmentArgs.fromBundle(requireArguments()).projectHistory)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.ganttChartGroup.setColorList(resources.getStringArray(R.array.color_array_1).toList(),resources.getStringArray(R.array.color_array_2).toList())
        binding.viewModel=viewModel
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

            override fun eventTaskSelect(taskPos: Int, taskValue: Task?) {
                viewModel.setTaskSelect(taskPos)
            }

            override fun eventTaskModify(taskPos: Int, task: Task) {
                viewModel.setNewTaskCondition(taskPos,task)

            }

            override fun eventTaskSwap(task: MutableList<Task>) {
                viewModel.setTasksSwap(task)
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
                binding.ganttChartGroup.setTaskPosSelect(it)
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

        binding.taskEdit.setOnClickListener {
            if (viewModel.isTaskSelect() > -1){
                viewModel.goToEditTask(viewModel.isTaskSelect())
            }
        }

        binding.taskDelete.setOnClickListener {
            if (viewModel.isTaskSelect() > -1){
                viewModel.taskRemove(viewModel.isTaskSelect())
            }
        }

        binding.taskCopy.setOnClickListener {
            if (viewModel.isTaskSelect() > -1){
                viewModel.copyTask()
            }
        }

        binding.taskUp.setOnClickListener {
            if (viewModel.isTaskSelect() > -1 && viewModel.isTaskSelect()!=0){
                viewModel.swapTaskUp()
            }
        }

        binding.taskDown.setOnClickListener {
            if (viewModel.isTaskSelect() > -1 && viewModel.isTaskSelect() != viewModel.projectRep[viewModel.projectPos].taskList.lastIndex){
                viewModel.swapTaskDown()
            }
        }

        viewModel.taskTimeScale.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.ganttChartGroup.setTaskActionTimeScale(it)
            }
        })

        viewModel.taskTimeScale.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.taskDay.isSelected = it == 0
                binding.taskHour.isSelected = it == 1
                binding.task15m.isSelected = it == 2
                binding.task5m.isSelected = it == 3
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            MaterialAlertDialogBuilder(requireNotNull(context))
                .setTitle("You are going to leave your project!")
                .setMessage("Press cancel to save your project.\nPress leave to continue leave.")
                .setNegativeButton("Cancel") { dialog, which ->

                }.setPositiveButton("Leave") { dialog, which ->
                    this@GanttFragment.findNavController().popBackStack()
                }.show()
        }

        return binding.root
    }

}