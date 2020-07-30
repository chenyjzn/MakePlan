package com.yuchen.makeplan.multigantt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiTask
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.databinding.FragmentMultiGanttBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.util.TimeUtil.StampToDate
import com.yuchen.makeplan.view.MultiGanttChart
import com.yuchen.makeplan.view.MultiGanttChartGroup
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class MultiGanttFragment : Fragment() {

    private val viewModel: MultiGanttViewModel by viewModels<MultiGanttViewModel> { getVmFactory(MultiGanttFragmentArgs.fromBundle(requireArguments()).multiProject)}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMultiGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.multiGanttChartGroup.setColorList(resources.getStringArray(R.array.color_array_1).toList(),resources.getStringArray(R.array.color_array_2).toList())
        binding.viewModel = viewModel

        var startTime = System.currentTimeMillis()
        var endTime = startTime + 7 * DAY_MILLIS
        var firstCreate = true
        binding.multiGanttChartGroup.setRange(startTime,endTime)

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(firstCreate){
                    if (it.isEmpty()) {
                        startTime = System.currentTimeMillis()
                        endTime = startTime + 7 * DAY_MILLIS
                        binding.multiGanttChartGroup.setRange(startTime,endTime)
                    }else{
                        startTime = Long.MAX_VALUE
                        endTime = 0L
                        for (i in it){
                            startTime = min(startTime, i.startTimeMillis)
                            endTime = max(endTime,i.endTimeMillis)
                        }
                        binding.multiGanttChartGroup.setRange(startTime,endTime)
                    }
                    firstCreate = false
                    Log.d("chenyjzn","start = ${StampToDate(startTime)}, end = ${StampToDate(endTime)}")
                }
                var fb = 0.0f
                var max = 0.0f
                for (i in it){
                    max += (i.endTimeMillis-i.startTimeMillis)
                    fb += (i.endTimeMillis-i.startTimeMillis)*(i.completeRate/100f)
                }
                if (max == 0.0f)
                    viewModel.updateProjectCompleteRate(0)
                else
                    viewModel.updateProjectCompleteRate ((fb*100f/max).roundToInt())
                binding.multiGanttChartGroup.setTaskList(it)
                viewModel.taskSelect.value?.let {id ->
                    viewModel.tasks.value?.let {list ->
                        var isTaskAlive = false
                        for (i in list){
                            if (i.firebaseId == id){
                                isTaskAlive = true
                                break
                            }
                        }
                        if (!isTaskAlive){
                            viewModel.setTaskSelect(null)
                        }
                    }
                }
                binding.multiGanttChartGroup.invalidate()
            }
        })

        viewModel.taskSelect.observe(viewLifecycleOwner, Observer {
            Log.d("chenyjzn","task select $it")
            binding.multiGanttChartGroup.settaskSelectFirebaseId(it)
            binding.multiGanttChartGroup.invalidate()
        })

        binding.multiGanttChartGroup.setOnEventListener(object :MultiGanttChartGroup.OnEventListener{
            override fun eventChartTime(startTimeMillis: Long, endTimeMillis: Long) {

            }

            override fun eventMoveDx(dx: Float, width: Int) {

            }

            override fun eventZoomDlDr(dl: Float, dr: Float, width: Int) {

            }

            override fun eventTaskSelect(taskFirebaseId: String?) {
                Log.d("chenyjzn","task event")
                viewModel.setTaskSelect(taskFirebaseId)
            }

            override fun eventTaskModify(taskFirebaseId: String?, task: MultiTask) {
                viewModel.updateTaskToFirebase(task)
            }

            override fun eventTaskSwap(task: MutableList<MultiTask>) {

            }

        })

        binding.multiGanttAddTask.setOnClickListener {
            if (viewModel.taskSelect.value == null && viewModel.project.value!= null)
                this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToMultiTaskFragment(viewModel.project.value!!,null))
        }

        binding.multiTaskEdit.setOnClickListener {
            viewModel.taskSelect.value?.let {id->
                viewModel.tasks.value?.let { list ->
                    viewModel.project.value?.let {project ->
                        val filterList = list.filter {
                            it.firebaseId == id
                        }
                        if (filterList.isNotEmpty()){
                            this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToMultiTaskFragment(project,filterList[0]))
                            viewModel.taskSelectClear()
                        }
                    }

                }
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
