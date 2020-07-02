package com.yuchen.makeplan.gantt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentGanttBinding
import java.util.*


class GanttFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGanttBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val application = requireNotNull(this.activity).application
        val viewModelFactory = GanttViewModelFactory(application)
        val viewModel = ViewModelProviders.of(this,viewModelFactory).get(GanttViewModel::class.java)

        val taskAdapter = GanttTaskAdapter(viewModel)
        binding.ganttTaskRecycler.adapter = taskAdapter

        viewModel.project.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                binding.ganttTimeLine.setRange(it.startTimeMillis,it.endTimeMillis)
                taskAdapter.projectStartTimeMillis = it.startTimeMillis
                taskAdapter.projectEndTimeMillis = it.endTimeMillis
                taskAdapter.submitList(it.taskList)
            }
        })

        return binding.root
    }
}
