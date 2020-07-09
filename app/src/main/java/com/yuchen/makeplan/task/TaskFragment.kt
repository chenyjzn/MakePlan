package com.yuchen.makeplan.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuchen.makeplan.databinding.FragmentTaskBinding
import com.yuchen.makeplan.ext.getVmFactory
import java.util.*


class TaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels<TaskViewModel> { getVmFactory(TaskFragmentArgs.fromBundle(requireArguments()).projectHistory,
        TaskFragmentArgs.fromBundle(requireArguments()).projectHistoryPos) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTaskBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val colorAdaptor = TaskColorAdapter(viewModel)
        binding.taskColorRecycler.adapter = colorAdaptor
        binding.taskColorRecycler.layoutManager = GridLayoutManager(binding.taskColorRecycler.context,6)
        viewModel.newProject.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(TaskFragmentDirections.actionTaskFragmentToGanttFragment(it.toTypedArray(),it.lastIndex))
            }
        })
        viewModel.newTask.observe(viewLifecycleOwner, Observer {
            Log.d("chenyjzn","new task = $it")
        })
        return binding.root
    }

}