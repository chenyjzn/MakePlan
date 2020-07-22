package com.yuchen.makeplan.multi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemMultiBinding
import com.yuchen.makeplan.ext.getVmFactory



class MultiItems : Fragment() {
    private val viewModel: MultiItemsViewModel by viewModels<MultiItemsViewModel> { getVmFactory(arguments?.getInt("object")?:0) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ItemMultiBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = MultiItemsAdapter()
        binding.itemMultiRecycler.adapter = adapter
        binding.itemMultiRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        adapter.setProjectClickListener(object : MultiItemsAdapter.ProjectClickListener{
            override fun onProjectClick(project: MultiProject) {
                when(viewModel.pagerPos){
                    PAGER_PROJECTS -> parentFragment?.findNavController()?.navigate(MultiFragmentDirections.actionMultiFragmentToMultiGanttFragment(project))
                    PAGER_SENDS -> {
                        MaterialAlertDialogBuilder(requireNotNull(context))
                            .setTitle("Cancel send request to ${project.name}?")
                            .setNeutralButton("Cancel") { dialog, which ->

                            }
                            .setPositiveButton("Yes") { dialog, which ->
                                viewModel.cancelUserToProject(project)
                            }.show()
                    }
                    PAGER_RECEIVE -> {
                        MaterialAlertDialogBuilder(requireNotNull(context))
                            .setTitle("Accept join request to ${project.name}?")
                            .setNeutralButton("Cancel") { dialog, which ->

                            }
                            .setNegativeButton("No") { dialog, which ->
                                viewModel.cancelProjectToUser(project)
                            }.setPositiveButton("Yes") { dialog, which ->
                                viewModel.acceptProjectToUser(project)
                            }.show()
                    }
                }
            }
            override fun onProjectLongClick(project: MultiProject) {
                when(viewModel.pagerPos){
                    PAGER_PROJECTS -> parentFragment?.findNavController()?.navigate(MultiFragmentDirections.actionMultiFragmentToMultiEditDialog(project))
                    PAGER_SENDS -> {}
                    PAGER_RECEIVE -> {}
                }
            }
        })
        return binding.root
    }

    companion object{
        const val PAGER_PROJECTS = 0
        const val PAGER_SENDS = 1
        const val PAGER_RECEIVE = 2
    }
}