package com.yuchen.makeplan.searchuser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.MainActivity
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.FragmentSearchUserBinding
import com.yuchen.makeplan.ext.getVmFactory

class SearchUserFragment : Fragment() {
    private val viewModel: SearchUserViewModel by viewModels { getVmFactory(SearchUserFragmentArgs.fromBundle(requireArguments()).project) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = SearchUserAdapter(viewModel)
        binding.searchUserRecycler.adapter = adapter
        binding.searchUserRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter.setOnSelectListener(object : SearchUserAdapter.OnSelectListener {
            override fun userSelect(user: User) {
                MaterialAlertDialogBuilder(requireNotNull(context))
                    .setTitle("Do you want to invite ${user.displayName} to project?")
                    .setNegativeButton("No") { dialog, which ->

                    }.setPositiveButton("Yes") { dialog, which ->
                        viewModel.requestProjectToUser(user)
                    }
                    .show()
            }
        })

        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                viewModel.myProject.value?.let { project ->
                    adapter.appendList(list, project)
                    adapter.filter.filter(viewModel.filterString)
                }
            }
        })

        viewModel.myProject.observe(viewLifecycleOwner, Observer {
            it?.let { project ->
                viewModel.users.value?.let { list ->
                    adapter.appendList(list, project)
                    adapter.filter.filter(viewModel.filterString)
                }
            }
        })

        binding.searchUserToolbar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }

        val searchView = binding.searchUserToolbar.menu.findItem(R.id.fun_search).actionView as SearchView
        searchView.queryHint = "Users Info"
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
                    binding.searchUserProgress.visibility = View.VISIBLE
                }
                is LoadingStatus.DONE -> {
                    binding.searchUserProgress.visibility = View.INVISIBLE
                }
                is LoadingStatus.ERROR -> {
                    (activity as MainActivity).showErrorMessage(it.message)
                }
            }
        })

        return binding.root
    }
}
