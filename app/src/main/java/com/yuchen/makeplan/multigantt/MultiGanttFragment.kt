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
    private var isFirstTime = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMultiGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.multiGanttMembers.setOnClickListener {
            this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToUsersFragment(MultiGanttFragmentArgs.fromBundle(requireArguments()).multiProject))
        }

        binding.multiGanttInvite.setOnClickListener {
            this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToInviteUsersFragment(MultiGanttFragmentArgs.fromBundle(requireArguments()).multiProject))
        }

        binding.multiGanttJoin.setOnClickListener {
            this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToJoinUserFragment(MultiGanttFragmentArgs.fromBundle(requireArguments()).multiProject))
        }

        viewModel.project.observe(viewLifecycleOwner, Observer {
            it?.let {
//                binding.multiGanttTimeLine.setRange(it.startTimeMillis,it.endTimeMillis)
//                binding.multiGanttTimeLine.invalidate()
//                binding.multiGanttChart.setRange(it.startTimeMillis,it.endTimeMillis)
//                binding.multiGanttChart.invalidate()
            }
        })

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            Log.d("chenyjzn","Task null check = $it")
            if (it == null && isFirstTime){
                var start= System.currentTimeMillis()
                var end : Long = start + 7 * DAY_MILLIS
                binding.multiGanttTimeLine.setRange(start,end)
                binding.multiGanttChart.setRange(start,end)
                binding.multiGanttTimeLine.invalidate()
                binding.multiGanttChart.invalidate()
                isFirstTime = false
            }
            it?.let {
                if (isFirstTime){
                    var start : Long = Long.MAX_VALUE
                    for (i in it){
                        if (i.startTimeMillis < start)
                            start = i.startTimeMillis
                    }
                    if (start == Long.MAX_VALUE)
                        start = System.currentTimeMillis()
                    var end : Long = start + 7 * DAY_MILLIS
                    binding.multiGanttTimeLine.setRange(start,end)
                    binding.multiGanttChart.setRange(start,end)
                    binding.multiGanttTimeLine.invalidate()
                    binding.multiGanttChart.invalidate()
                    isFirstTime = false
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

                binding.multiGanttChart.setTaskList(it)
                binding.multiGanttChart.invalidate()
            }
        })

        viewModel.taskSelect.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.multiGanttChart.setTaskSelect(it)
                binding.multiGanttChart.invalidate()
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

        binding.multiGanttChart.setOnEventListener(object : MultiGanttChart.OnEventListener{
            override fun eventMoveDx(dx: Float, width: Int) {
                binding.multiGanttTimeLine.setProjectTimeByDx(dx, width)
                binding.multiGanttTimeLine.invalidate()
            }

            override fun eventZoomDlDr(dl: Float, dr: Float, width: Int) {
                binding.multiGanttTimeLine.setProjectTimeByDlDr(dl, dr, width)
                binding.multiGanttTimeLine.invalidate()
            }

            override fun eventTaskSelect(task: MultiTask?) {
                viewModel.setTaskSelect(task)
            }
        })

        binding.testButton.setOnClickListener {
            this.findNavController().navigate(MultiGanttFragmentDirections.actionMultiGanttFragmentToMembersFragment(MultiGanttFragmentArgs.fromBundle(requireArguments()).multiProject))
        }

        return binding.root
    }

}
