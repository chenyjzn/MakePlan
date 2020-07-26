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
                        viewModel.requestProjectToUser(user)
                    }
                    .show()
            }
        })
        binding.searchUserRecycler.adapter = adapter
        binding.searchUserRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {list ->
                viewModel.myProject.value?.let {project ->
                    val newList = list.filter {
                        var notMember = true
                        for (i in project.receiveUid){
                            if (i == it.uid){
                                notMember = false
                                break
                            }
                        }
                        if (notMember) {
                            for (i in project.membersUid) {
                                if (i == it.uid){
                                    notMember = false
                                    break
                                }
                            }
                        }
                        if (notMember){
                            for (i in project.sendUid){
                                if (i == it.uid){
                                    notMember = false
                                    break
                                }
                            }
                        }
                        notMember
                    }
                    adapter.appendList(newList)
                }
                if (viewModel.myProject.value == null)
                    adapter.appendList(list)

                adapter.notifyDataSetChanged()
            }
        })

        viewModel.myProject.observe(viewLifecycleOwner, Observer {
            it?.let {project->
                viewModel.users.value?.let {list ->
                    val newList = list.filter {
                        var notMember = true
                        for (i in project.receiveUid){
                            if (i == it.uid){
                                notMember = false
                                break
                            }
                        }
                        if (notMember) {
                            for (i in project.membersUid) {
                                if (i == it.uid){
                                    notMember = false
                                    break
                                }
                            }
                        }
                        if (notMember){
                            for (i in project.sendUid){
                                if (i == it.uid){
                                    notMember = false
                                    break
                                }
                            }
                        }
                        notMember
                    }
                    adapter.appendList(newList)
                    adapter.notifyDataSetChanged()
                }
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
