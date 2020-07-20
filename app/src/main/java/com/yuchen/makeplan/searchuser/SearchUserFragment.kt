package com.yuchen.makeplan.searchuser

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
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.FragmentSearchUserBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.users.UsersAdapter

class SearchUserFragment : Fragment() {

    private val viewModel: SearchUserViewModel by viewModels<SearchUserViewModel> { getVmFactory(SearchUserFragmentArgs.fromBundle(requireArguments()).project)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = SearchUsersAdapter()
        adapter.setOnSelectListener(object :SearchUsersAdapter.OnSelectListener{
            override fun userSelect(user: User) {
                MaterialAlertDialogBuilder(requireNotNull(context))
                    .setTitle("Do you want to invite ${user.displayName} to project?")
                    .setNegativeButton("No") { dialog, which ->

                    }.setPositiveButton("Yes") { dialog, which ->
                        viewModel.inviteUserToProject(user)
                    }
                    .show()
            }
        })
        binding.searchUserRecycler.adapter = adapter
        binding.searchUserRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.appendList(it)
            }
        })

        binding.searchUserSearch.setIconifiedByDefault(true)
        binding.searchUserSearch.setSubmitButtonEnabled(true)
        binding.searchUserSearch.onActionViewExpanded()
        binding.searchUserSearch.setIconifiedByDefault(true)
        binding.searchUserSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
