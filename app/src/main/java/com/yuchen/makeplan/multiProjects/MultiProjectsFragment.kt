package com.yuchen.makeplan.multiProjects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.FragmentMultiProjectsBinding
import com.yuchen.makeplan.ext.getVmFactory

class MultiProjectsFragment : Fragment() {

    private val viewModel: MultiProjectsViewModel by viewModels<MultiProjectsViewModel> { getVmFactory() }
    lateinit var binding: FragmentMultiProjectsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMultiProjectsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val projectsAdapter = MultiProjectsAdapter()
        projectsAdapter.setItemClickListener(object : MultiProjectsAdapter.OnClickListener{
            override fun onProjectClick(project: MultiProject) {
                viewModel.goToGantt(project)
            }
            override fun onProjectLongClick(project: MultiProject) {
                viewModel.goToProjectSetting(project)
            }
        })

        binding.multiProjectsRecycler.adapter = projectsAdapter

        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                projectsAdapter.appendList(it)
                projectsAdapter.notifyDataSetChanged()
            }
        })

        viewModel.navigateToGantt.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(MultiProjectsFragmentDirections.actionMultiProjectsFragmentToMultiGanttFragment(it))
                viewModel.goToGanttDone()
            }
        })

        viewModel.navigateToProjectSetting.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(MultiProjectsFragmentDirections.actionMultiProjectsFragmentToMultiEditDialog(it))
                viewModel.goToProjectSettingDone()
            }
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    LoadingStatus.LOADING ->{
                        binding.multiProjectsProgress.visibility = View.VISIBLE
                    }
                    LoadingStatus.DONE -> {
                        binding.multiProjectsProgress.visibility = View.INVISIBLE
                    }
                    LoadingStatus.ERROR -> {

                    }
                }
            }
        })

        binding.multiProjectsAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search_project -> {
                    this.findNavController().navigate(MultiProjectsFragmentDirections.actionMultiProjectsFragmentToSearchFragment())
                    true
                }
                R.id.add_project -> {
                    this.findNavController().navigate(MultiProjectsFragmentDirections.actionMultiProjectsFragmentToMultiEditDialog(null))
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

}
