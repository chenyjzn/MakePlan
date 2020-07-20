package com.yuchen.makeplan.search

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.FragmentSearchBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.multiProjects.MultiProjectsAdapter
import com.yuchen.makeplan.projects.ProjectsAdapter


class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels<SearchViewModel> { getVmFactory() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = SearchAdapter()
        adapter.setItemClickListener(object : SearchAdapter.OnClickListener{
            override fun onProjectClick(project: MultiProject) {
                MaterialAlertDialogBuilder(requireNotNull(context))
                    .setTitle("Send join request to ${project.name}?")
                    .setNegativeButton("No") { dialog, which ->

                    }.setPositiveButton("Yes") { dialog, which ->
                        viewModel.sendJoinRequest(project)
                    }
                    .show()
            }
            override fun onProjectLongClick(project: MultiProject) {
                TODO("Not yet implemented")
            }
        })
        binding.searchRecycler.adapter = adapter
        binding.searchRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.appendList(it)
                adapter.filter.filter("")
            }
        })
        binding.searchEdit.setIconifiedByDefault(true)
        binding.searchEdit.setSubmitButtonEnabled(true)
        binding.searchEdit.onActionViewExpanded()
        binding.searchEdit.setIconifiedByDefault(true)
        binding.searchEdit.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }

        })

        return binding.root
    }
}