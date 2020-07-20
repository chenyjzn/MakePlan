package com.yuchen.makeplan.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.FragmentUsersBinding
import com.yuchen.makeplan.ext.getVmFactory

class UsersFragment : Fragment() {

    private val viewModel: UsersViewModel by viewModels<UsersViewModel> { getVmFactory(UsersFragmentArgs.fromBundle(requireArguments()).project)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = UsersAdapter()
        adapter.setOnSelectListener(object :UsersAdapter.OnSelectListener{
            override fun userSelect(user: User) {
                MaterialAlertDialogBuilder(requireNotNull(context))
                    .setTitle("Remove member ${user.displayName}?")
                    .setNegativeButton("No") { dialog, which ->

                    }.setPositiveButton("Yes") { dialog, which ->
                        viewModel.removeProjectUser(user)
                    }
                    .show()
            }
        })
        binding.usersRecycler.adapter = adapter
        binding.usersRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitMembers(it)
            }
        })
        return binding.root
    }
}
