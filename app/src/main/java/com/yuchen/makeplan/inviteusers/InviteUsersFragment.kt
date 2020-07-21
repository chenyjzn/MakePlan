package com.yuchen.makeplan.inviteusers

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.FragmentInviteUsersBinding
import com.yuchen.makeplan.ext.getVmFactory

class InviteUsersFragment : Fragment() {

    private val viewModel: InviteUsersViewModel by viewModels<InviteUsersViewModel> { getVmFactory(InviteUsersFragmentArgs.fromBundle(requireArguments()).project)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInviteUsersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = InviteUsersAdapter()
        adapter.setOnSelectListener(object :InviteUsersAdapter.OnSelectListener{
            override fun userSelect(user: User) {
                    MaterialAlertDialogBuilder(requireNotNull(context))
                        .setTitle("Cancel member invite ${user.displayName}?")
                        .setNegativeButton("No") { dialog, which ->

                        }.setPositiveButton("Yes") { dialog, which ->
                            viewModel.cancelInvite(user)
                        }
                        .show()
            }
        })
        binding.inviteUserRecycler.adapter = adapter
        binding.inviteUserRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("chenyjzn","$it")
                adapter.submitMembers(it)
            }
        })

        binding.inviteUserProjectsAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    this.findNavController().navigate(InviteUsersFragmentDirections.actionInviteUsersFragmentToSearchUserFragment(InviteUsersFragmentArgs.fromBundle(requireArguments()).project))
                    true
                }
                else -> false
            }
        }

        binding.inviteUserProjectsAppBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }

        return binding.root
    }
}
