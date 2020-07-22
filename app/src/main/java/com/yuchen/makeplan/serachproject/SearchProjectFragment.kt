package com.yuchen.makeplan.serachproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.FragmentSearchProjectBinding
import com.yuchen.makeplan.ext.getVmFactory

class SearchProjectFragment : Fragment() {
    private val viewModel: SearchProjectViewModel by viewModels<SearchProjectViewModel> { getVmFactory() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchProjectBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = SearchProjectAdapter()
        adapter.setItemClickListener(object : SearchProjectAdapter.OnClickListener{
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
        binding.searchProjectRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.appendList(it)
                adapter.filter.filter("")
            }
        })
        binding.searchProjectEdit.setIconifiedByDefault(true)
        binding.searchProjectEdit.setSubmitButtonEnabled(true)
        binding.searchProjectEdit.onActionViewExpanded()
        binding.searchProjectEdit.setIconifiedByDefault(true)
        binding.searchProjectEdit.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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