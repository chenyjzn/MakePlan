package com.yuchen.makeplan.joinuser

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
import com.yuchen.makeplan.databinding.FragmentJoinUserBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.users.UsersAdapter

class JoinUserFragment : Fragment() {

    private val viewModel: JoinUserViewModel by viewModels<JoinUserViewModel> { getVmFactory(JoinUserFragmentArgs.fromBundle(requireArguments()).project)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentJoinUserBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = JoinUserAdapter()
        adapter.setOnSelectListener(object :JoinUserAdapter.OnSelectListener{
            override fun userSelect(user: User) {
                MaterialAlertDialogBuilder(requireNotNull(context))
                    .setTitle("Do you want to invite ${user.displayName} to project?")
                    .setNegativeButton("No") { dialog, which ->

                    }.setPositiveButton("Yes") { dialog, which ->
                        viewModel.confirmUserJoin(user)
                    }
                    .show()
            }
        })
        binding.joinUserRecycler.adapter = adapter
        binding.joinUserRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitMembers(it)
            }
        })
        return binding.root
    }
}
