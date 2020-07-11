package com.yuchen.makeplan.projects

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentProjectsBinding
import com.yuchen.makeplan.ext.getVmFactory

class ProjectsFragment : Fragment() {

    private val viewModel: ProjectsViewModel by viewModels<ProjectsViewModel> { getVmFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentProjectsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val projectsAdapter = ProjectsAdapter(viewModel)

        binding.projectsRecycler.adapter = projectsAdapter

        binding.projectsBackGround.setBackgroundColor(resources.getColor(R.color.projects_card_EBECF0))

        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                projectsAdapter.submitProjects(it)
//                Log.d("chenyjzn", "$it")
            }
        })

        viewModel.navigateToGantt.observe(viewLifecycleOwner, Observer {
            it?.let {
//                Log.d("chenyjzn", "go to gantt $it")
                this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToGanttFragment(arrayOf(it),0))
                viewModel.goToGanttDone()
            }
        })

        viewModel.navigateToProjectSetting.observe(viewLifecycleOwner, Observer {
            it?.let {
//                Log.d("chenyjzn", "go to project setting $it")
                viewModel.goToProjectSettingDone()
            }
        })

        return binding.root
    }
}
