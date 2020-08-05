package com.yuchen.makeplan.multigantt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiTask
import com.yuchen.makeplan.databinding.FragmentMultiGanttBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.view.MultiGanttChartGroup
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class MultiGanttFragment : Fragment() {

    private val viewModel: MultiGanttViewModel by viewModels<MultiGanttViewModel> { getVmFactory(MultiGanttFragmentArgs.fromBundle(requireArguments()).multiProject) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMultiGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.multiGanttChartGroup.setColorList(
            resources.getStringArray(R.array.color_array_1).toList(),
            resources.getStringArray(R.array.color_array_2).toList()
        )
        binding.viewModel = viewModel

        var startTime = System.currentTimeMillis()
        var endTime = startTime + 7 * DAY_MILLIS
        var firstCreate = true
        binding.multiGanttChartGroup.setRange(startTime, endTime)

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    LoadingStatus.LOADING -> {
                        binding.multiGanttMembers.isClickable = false
                        binding.multiTaskCopy.isClickable = false
                        binding.multiTaskDelete.isClickable = false
                        binding.multiTaskEdit.isClickable = false
                        binding.multiGanttAddTask.isClickable = false
                        binding.multiTaskDay.isClickable = false
                        binding.multiTask15m.isClickable = false
                        binding.multiTask5m.isClickable = false
                        binding.multiTaskHour.isClickable = false
                        binding.multiGanttChartGroup.isTouchAble = false
//                        binding.multiGanttProgress.visibility = View.VISIBLE
                    }
                    LoadingStatus.DONE -> {
                        binding.multiGanttMembers.isClickable = true
                        binding.multiTaskCopy.isClickable = true
                        binding.multiTaskDelete.isClickable = true
                        binding.multiTaskEdit.isClickable = true
                        binding.multiGanttAddTask.isClickable = true
                        binding.multiTaskDay.isClickable = true
                        binding.multiTask15m.isClickable = true
                        binding.multiTask5m.isClickable = true
                        binding.multiTaskHour.isClickable = true
                        binding.multiGanttChartGroup.isTouchAble = true
//                        binding.multiGanttProgress.visibility = View.INVISIBLE
                    }
                    LoadingStatus.ERROR -> {
                        binding.multiGanttMembers.isClickable = true
                        binding.multiTaskCopy.isClickable = true
                        binding.multiTaskDelete.isClickable = true
                        binding.multiTaskEdit.isClickable = true
                        binding.multiGanttAddTask.isClickable = true
                        binding.multiTaskDay.isClickable = true
                        binding.multiTask15m.isClickable = true
                        binding.multiTask5m.isClickable = true
                        binding.multiTaskHour.isClickable = true
                        binding.multiGanttChartGroup.isTouchAble = true
//                        binding.multiGanttProgress.visibility = View.INVISIBLE
                    }
                }
            }
        })

        viewModel.project.observe(viewLifecycleOwner, Observer {
            it?.let {
                val count = it.receiveUid.size
                if (count == 0) {
                    binding.multiGanttNotify.visibility = View.INVISIBLE
                } else if (count >= 999) {
                    binding.multiGanttNotify.visibility = View.VISIBLE
                    binding.multiGanttNotify.text = "999+"
                } else {
                    binding.multiGanttNotify.visibility = View.VISIBLE
                    binding.multiGanttNotify.text = count.toString()
                }
            }
        })

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (firstCreate) {
                    if (it.isEmpty()) {
                        startTime = System.currentTimeMillis()
                        endTime = startTime + 7 * DAY_MILLIS
                        binding.multiGanttChartGroup.setRange(startTime, endTime)
                    } else {
                        startTime = Long.MAX_VALUE
                        endTime = 0L
                        for (i in it) {
                            startTime = min(startTime, i.startTimeMillis)
                            endTime = max(endTime, i.endTimeMillis)
                        }
                        binding.multiGanttChartGroup.setRange(startTime, endTime)
                    }
                    firstCreate = false
                }
                var fb = 0.0f
                var max = 0.0f
                for (i in it) {
                    max += (i.endTimeMillis - i.startTimeMillis)
                    fb += (i.endTimeMillis - i.startTimeMillis) * (i.completeRate / 100f)
                }
                if (max == 0.0f)
                    viewModel.updateProjectCompleteRate(0)
                else
                    viewModel.updateProjectCompleteRate((fb * 100f / max).roundToInt())

                binding.multiGanttChartGroup.setTaskList(it)
                binding.multiGanttChartGroup.invalidate()
            }
        })

        viewModel.taskSelect.observe(viewLifecycleOwner, Observer {
            binding.multiGanttChartGroup.setTaskValueSelect(it)
            binding.multiGanttChartGroup.invalidate()
        })

        binding.multiGanttChartGroup.setOnEventListener(object :
            MultiGanttChartGroup.OnEventListener {

            override fun eventTaskSelect(taskValue: MultiTask?) {
                viewModel.setTaskSelect(taskValue)
            }

            override fun eventTaskModify(task: MultiTask) {
                viewModel.updateTaskToFirebase(task)
            }
        })

        binding.multiGanttAddTask.setOnClickListener {
            if (viewModel.taskSelect.value == null && viewModel.project.value != null)
                this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToMultiTaskFragment(viewModel.project.value!!, null))
        }

        binding.multiTaskEdit.setOnClickListener {
            if (viewModel.taskSelect.value != null && viewModel.project.value != null) {
                this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToMultiTaskFragment(viewModel.project.value!!, viewModel.taskSelect.value!!))
                viewModel.taskSelectClear()
            }
        }

        binding.multiTaskDelete.setOnClickListener {
            if (viewModel.taskSelect.value != null)
                viewModel.taskRemove()
        }

        viewModel.taskTimeScale.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.multiGanttChartGroup.setTaskActionTimeScale(it)
            }
        })

        viewModel.taskTimeScale.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.multiTaskDay.isSelected = it == 0
                binding.multiTaskHour.isSelected = it == 1
                binding.multiTask15m.isSelected = it == 2
                binding.multiTask5m.isSelected = it == 3
            }
        })

        binding.multiTaskCopy.setOnClickListener {
            if (viewModel.taskSelect.value != null)
                viewModel.copyTaskToFirebase()
        }

        binding.multiGanttMembers.setOnClickListener {
            this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToMembersFragment(MultiGanttFragmentArgs.fromBundle(requireArguments()).multiProject))
        }

        return binding.root
    }
}