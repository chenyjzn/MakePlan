package com.yuchen.makeplan.serachproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.MainActivity
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.FragmentSearchProjectBinding
import com.yuchen.makeplan.ext.getVmFactory

class SearchProjectFragment : Fragment() {
    private val viewModel: SearchProjectViewModel by viewModels { getVmFactory() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchProjectBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = SearchProjectAdapter(viewModel)
        adapter.setItemClickListener(object : SearchProjectAdapter.OnClickListener {
            override fun onProjectClick(project: MultiProject) {
                MaterialAlertDialogBuilder(requireNotNull(context))
                    .setTitle("Send join request to ${project.name}?")
                    .setNegativeButton("No") { dialog, which ->

                    }.setPositiveButton("Yes") { dialog, which ->
                        viewModel.requestUserToProject(project)
                    }
                    .show()
            }

            override fun onProjectLongClick(project: MultiProject) {
                TODO("Not yet implemented")
            }
        })

        binding.searchProjectRecycler.adapter = adapter
        binding.searchProjectRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.appendList(it)
                adapter.filter.filter(viewModel.filterString)
            }
        })

        binding.searchProjectToolbar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }

        val searchView = binding.searchProjectToolbar.menu.findItem(R.id.fun_search).actionView as SearchView
        searchView.queryHint = "Project or Members Info"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterString = newText ?: ""
                adapter.filter.filter(viewModel.filterString)
                return true
            }
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoadingStatus.LOADING -> {
                    binding.searchProjectProgress.visibility = View.VISIBLE
                }
                is LoadingStatus.DONE -> {
                    binding.searchProjectProgress.visibility = View.INVISIBLE
                }
                is LoadingStatus.ERROR -> {
                    (activity as MainActivity).showErrorMessage(it.message)
                }
            }
        })

        return binding.root
    }
}