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
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemNotifyBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.multiProjects.MultiProjectsViewModel


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
        adapter.setItemClickListener(object : NotifyItemsAdapter.OnClickListener{
            override fun onProjectClick(project: MultiProject) {
                if (viewModel.notifyPos == 0){
                    MaterialAlertDialogBuilder(requireNotNull(context))
                        .setTitle("Cancel send request?")
                        .setNegativeButton("No") { dialog, which ->

                        }.setPositiveButton("Yes") { dialog, which ->

                        }
                        .show()
                }else{
                    MaterialAlertDialogBuilder(requireNotNull(context))
                        .setTitle("Confirm project invite request?")
                        .setNegativeButton("No") { dialog, which ->

                        }.setPositiveButton("Yes") { dialog, which ->

                        }
                        .show()
                }
            }
            override fun onProjectLongClick(project: MultiProject) {

            }
        })

        return binding.root
    }
}