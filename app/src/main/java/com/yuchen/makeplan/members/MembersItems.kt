package com.yuchen.makeplan.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.ItemMembersBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.util.UserManager


class MembersItems : Fragment() {
    private val viewModel: MembersItemsViewModel by viewModels<MembersItemsViewModel> { getVmFactory(arguments?.getParcelable("project")?:MultiProject(),arguments?.getInt("object")?:0) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ItemMembersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = MembersItemsAdapter()
        binding.membersRecycler.adapter = adapter
        binding.membersRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        viewModel.usersUid.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (viewModel.pagerPos==0){
                   if(it.filter { it == UserManager.user.uid }.isEmpty()){
                       parentFragment?.findNavController()?.navigate(MembersFragmentDirections.actionGlobalMultiProjectsFragment())
                   }
                }
                viewModel.setUsersByUidList(it)
            }
        })

        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        adapter.setUserClickListener(object : MembersItemsAdapter.UserClickListener{
            override fun userSelect(user: User) {
                when(viewModel.pagerPos){
                    PAGER_MEMBERSS -> {
                        if (!viewModel.isMembersOverOne()){
                            MaterialAlertDialogBuilder(requireNotNull(context))
                                .setTitle("You are the last member!!")
                                .setMessage("If you want to exist this project, please remove the project")
                                .setPositiveButton("Yes") { dialog, which ->

                                }.show()
                        }else if(user.uid == UserManager.user.uid){
                            MaterialAlertDialogBuilder(requireNotNull(context))
                                .setTitle("Exist this project?")
                                .setNeutralButton("Cancel") { dialog, which ->

                                }.setPositiveButton("Yes") { dialog, which ->
                                    viewModel.existProject()
                                }.show()
                        }
                    }
                    PAGER_SENDS -> {
                        MaterialAlertDialogBuilder(requireNotNull(context))
                            .setTitle("Cancel send request to ${user.displayName}?")
                            .setNegativeButton("No") { dialog, which ->

                            }.setPositiveButton("Yes") { dialog, which ->
                                viewModel.cancelProjectToUser(user)
                            }.show()
                    }
                    PAGER_RECEIVE -> {
                        MaterialAlertDialogBuilder(requireNotNull(context))
                            .setTitle("Accept ${user.displayName} join?")
                            .setNeutralButton("Cancel") { dialog, which ->

                            }.setNegativeButton("No") { dialog, which ->
                                viewModel.cancelUserToProject(user)
                            }.setPositiveButton("Yes") { dialog, which ->
                                viewModel.acceptUserToProject(user)
                            }.show()
                    }
                }
            }
        })

        return binding.root
    }

    companion object{
        const val PAGER_MEMBERSS = 0
        const val PAGER_SENDS = 1
        const val PAGER_RECEIVE = 2
    }
}