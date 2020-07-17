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
import com.yuchen.makeplan.databinding.FragmentGanttBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.view.GanttChart
import java.util.*
import kotlin.math.hypot
import kotlin.math.pow


class GanttFragment : Fragment() {

    private val viewModel: GanttViewModel by viewModels<GanttViewModel> { getVmFactory(GanttFragmentArgs.fromBundle(requireArguments()).projectHistory,GanttFragmentArgs.fromBundle(requireArguments()).isMultiProject)}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.project.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.ganttTimeLine.setRange(it.startTimeMillis,it.endTimeMillis)
                binding.ganttTimeLine.invalidate()
                binding.ganttChart.setProject(it)
                binding.ganttChart.invalidate()
            }
        })

        viewModel.navigateToTaskSetting.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(GanttFragmentDirections.actionGanttFragmentToTaskFragment(viewModel.getUndoListArray(),it,viewModel.isMultiProject))
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
//                Log.d("chenyjzn","Task select = $it")
                binding.ganttChart.setTaskSelect(it)
                binding.ganttChart.invalidate()
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

        var x0 = 0f
        var y0 = 0f
        var x1 = 0f
        var y1 = 0f
        var c = Calendar.getInstance()
        var touchStart = c.timeInMillis
        var touchStatus = TouchMode.NONE

        binding.ganttChart.setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_DOWN -> {
                    x0 = event.x
                    y0 = event.y
                    c = Calendar.getInstance()
                    touchStart = c.timeInMillis
                    touchStatus = TouchMode.CLICK
//                    Log.d("chenyjzn","touch1, x = $x0 , y = $y0")
                    true
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    x1 = event.getX(1)
                    y1 = event.getY(1)
                    touchStatus = TouchMode.ZOOM
//                    Log.d("chenyjzn","touch2, x = $x1 , y = $y1")
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1) {
                        if (touchStatus == TouchMode.MOVE) {
                            (v as GanttChart).setYPos(event.y - y0)
                            viewModel.setProjectTimeByDx(event.x - x0, v.width)
                        } else if (touchStatus != TouchMode.NONE && (event.y - y0).pow(2) + (event.x - x0).pow(2) > 6.0f) {
                            touchStatus = TouchMode.MOVE
                            (v as GanttChart).setYPos(event.y - y0)
                            viewModel.setProjectTimeByDx(event.x - x0, v.width)
                        }
                        x0 = event.x
                        y0 = event.y
                    }else if (event.pointerCount == 2){
                        if (touchStatus == TouchMode.ZOOM) {
                            val centerX = (x0 + x1) / 2
                            val centerY = (y0 + y1) / 2
                            val oldXR: Float
                            val oldYR: Float
                            val newXR: Float
                            val nerYR: Float
                            val oldXL: Float
                            val oldYL: Float
                            val newXL: Float
                            val nerYL: Float
                            if (x1 > x0) {
                                oldXR = x1
                                oldYR = y1
                                oldXL = x0
                                oldYL = y0
                                newXR = event.getX(1)
                                nerYR = event.getY(1)
                                newXL = event.getX(0)
                                nerYL = event.getY(0)
                            } else {
                                oldXR = x0
                                oldYR = y0
                                oldXL = x1
                                oldYL = y1
                                newXR = event.getX(0)
                                nerYR = event.getY(0)
                                newXL = event.getX(1)
                                nerYL = event.getY(1)
                            }
                            val dl = hypot(newXL - centerX, nerYL - centerY) - hypot(
                                (oldXL - centerX),
                                (oldYL - centerY)
                            )
                            val dr = hypot(newXR - centerX, nerYR - centerY) - hypot(
                                (oldXR - centerX),
                                (oldYR - centerY)
                            )
//                            Log.d("chenyjzn", "dl = $dl , dr = $dr")
                            viewModel.setProjectTimeByDlDr(dl, dr, v.width)
                            x0 = event.getX(0)
                            x1 = event.getX(1)
                            y0 = event.getY(0)
                            y1 = event.getY(1)
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP ->{
                    if (touchStatus == TouchMode.CLICK){
                        c = Calendar.getInstance()
                        if (c.timeInMillis - touchStart < MAX_CLICK_DURATION){
//                            Log.d("chenyjzn","Touch up x = ${event.x} , pos y = ${event.y} click")
                            viewModel.setTaskSelect((v as GanttChart).posTaskSelect(event.x,event.y))
                        }
                    }
                    touchStatus = TouchMode.NONE
                    false
                }
                else -> {
                    true
                }
            }
        }

        return binding.root
    }

    companion object{
        const val MAX_CLICK_DURATION = 400
        enum class TouchMode {
            CLICK,
            LONG_CLICK,
            MOVE,
            ZOOM,
            NONE
        }
    }
}