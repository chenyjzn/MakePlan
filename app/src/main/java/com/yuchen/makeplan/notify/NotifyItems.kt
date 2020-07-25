package com.yuchen.makeplan.notify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemNotifyBinding
import com.yuchen.makeplan.ext.getVmFactory


class NotifyItems : Fragment() {
    private val viewModel: NotifyItemsViewModel by viewModels<NotifyItemsViewModel> { getVmFactory(arguments?.getInt("object")?:0) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ItemNotifyBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = NotifyItemsAdapter()
        binding.itemNotifyRecycler.adapter = adapter
        binding.itemNotifyRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        adapter.setProjectClickListener(object : NotifyItemsAdapter.ProjectClickListener{
            override fun onProjectClick(project: MultiProject) {
                when(viewModel.pagerPos){
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
                    PAGER_SENDS -> {}
                    PAGER_RECEIVE -> {}
                }
            }
        })
        return binding.root
    }

    companion object{
        const val PAGER_SENDS = 0
        const val PAGER_RECEIVE = 1
    }
}