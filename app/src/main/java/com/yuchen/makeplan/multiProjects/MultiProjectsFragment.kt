package com.yuchen.makeplan.multiProjects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.FragmentMultiBinding
import com.yuchen.makeplan.databinding.FragmentMultiProjectsBinding
import com.yuchen.makeplan.ext.getVmFactory

class MultiProjectsFragment : Fragment() {
    private val viewModel: MultiProjectsViewModel by viewModels<MultiProjectsViewModel> { getVmFactory() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMultiProjectsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = MultiProjectsAdapter()
        binding.multiProjectsRecycler.adapter = adapter
        binding.multiProjectsRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        adapter.setProjectClickListener(object : MultiProjectsAdapter.ProjectClickListener{
            override fun onProjectClick(project: MultiProject) {
                this@MultiProjectsFragment.findNavController().navigate(MultiProjectsFragmentDirections.actionMultiProjectsFragmentToMultiGanttFragment(project))
            }
            override fun onProjectLongClick(project: MultiProject) {
                this@MultiProjectsFragment.findNavController().navigate(MultiProjectsFragmentDirections.actionMultiProjectsFragmentToMultiEditDialog(project))
            }
        })
        binding.multiProjectsAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.search_project -> {
                    this.findNavController().navigate(MultiProjectsFragmentDirections.actionMultiProjectsFragmentToSearchProjectFragment())
                    true
                }
                R.id.add_project-> {
                    this.findNavController().navigate(MultiProjectsFragmentDirections.actionMultiProjectsFragmentToMultiEditDialog(null))
                    true
                }
                else -> false
            }
        }
        return binding.root
    }
}
