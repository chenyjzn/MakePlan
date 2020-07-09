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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.FragmentGanttBinding
import com.yuchen.makeplan.ext.getVmFactory
import kotlin.math.sqrt


class GanttFragment : Fragment() {

    private val viewModel: GanttViewModel by viewModels<GanttViewModel> { getVmFactory(GanttFragmentArgs.fromBundle(requireArguments()).projectHistory,
        GanttFragmentArgs.fromBundle(requireArguments()).projectHistoryPos) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val taskAdapter = GanttTaskAdapter()
        binding.ganttTaskRecycler.adapter = taskAdapter

        val toolAdapter = GanttToolBarAdapter(viewModel)
        binding.ganttToolBar.adapter = toolAdapter
        binding.ganttToolBar.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        viewModel.project.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                binding.ganttTimeLine.setRange(it.startTimeMillis,it.endTimeMillis)
                binding.ganttTimeLine.invalidate()
                taskAdapter.submitProject(it)
            }
        })

        viewModel.navigateToTaskSetting.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(GanttFragmentDirections.actionGanttFragmentToTaskFragment(viewModel.projectRep.toTypedArray(),it))
                viewModel.goToTaskDone()
            }
        })

        var x = 0f
        var y = 0f
        var x1 = 0f
        var y1 = 0f

        binding.ganttTimeLine.setOnTouchListener { v, event ->
            var newX = event.x
            //Log.d("chenyjzn", "ganttTimeLine Touch x = $x")
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    if (event.pointerCount == 1) {
                        x = newX
                        Log.d("chenyjzn","ACTION_DOWN 1")
                    }
                    else if (event.pointerCount == 2) {
                        Log.d("chenyjzn","ACTION_DOWN 2")
                        val xTemp = event.getX(0) - event.getX(1)
                        val yTemp = event.getY(0) - event.getY(1)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1) {
                        viewModel.setProjectTimeByDx(newX - x, v.width)
                        x = newX
                        Log.d("chenyjzn","ACTION_MOVE 1")
                    }else if (event.pointerCount == 2){
                        Log.d("chenyjzn","ACTION_MOVE 2")
                        val xTemp = event.getX(0) - event.getX(1)
                        val yTemp = event.getY(0) - event.getY(1)
                    }
                }
                MotionEvent.ACTION_UP ->{
                    if (event.pointerCount == 1) {
                        viewModel.setProjectTimeByDx(newX - x, v.width)
                        x = newX
                        Log.d("chenyjzn","ACTION_UP 1")
                    }else if (event.pointerCount == 2){
                        Log.d("chenyjzn","ACTION_UP 2")
                        val xTemp = event.getX(0) - event.getX(1)
                        val yTemp = event.getY(0) - event.getY(1)
                    }
                }
                else -> {

                }
            }
            true
        }

        var mActivePointerId: Int = 0

        binding.ganttTaskRecycler.setOnTouchListener { v, event ->
            Log.d("chenyjzn", "Action : ${event.action}")
            if (event.pointerCount > 1) {
                Log.d("chenyjzn", "Multitouch event : ${event.pointerCount}")

            } else {
                // Single touch event
                Log.d("chenyjzn", "Single touch event")
            }
            when(event.actionMasked){
                MotionEvent.ACTION_DOWN -> {
                    if (event.pointerCount >= 1) {
                        x = event.getX(0)
                        y = event.getY(0)
                        Log.d("chenyjzn","ACTION_DOWN 1")
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    Log.d("chenyjzn","ACTION_DOWN 2")
                    x1 = event.getX(1)
                    y1 = event.getY(1)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1) {
                        viewModel.setProjectTimeByDx(event.getX(0) - x, v.width)
                        x = event.getX(0)
                        Log.d("chenyjzn","ACTION_MOVE 1")
                    }else if (event.pointerCount == 2){
                        Log.d("chenyjzn","ACTION_MOVE 2")
                        viewModel.setProjectTimeBy2Dx(event.getX(0) - x,event.getX(1) -x1, v.width)
                        x = event.getX(0)
                        x1 = event.getX(1)
                    }
                }
                MotionEvent.ACTION_UP ->{
                    if (event.pointerCount == 1) {

                    }else if (event.pointerCount == 2){

                    }
                }
                else -> {

                }
            }
            event.pointerCount > 1
        }

        return binding.root
    }
}