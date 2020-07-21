package com.yuchen.makeplan.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemMembersBinding
import com.yuchen.makeplan.databinding.ItemNotifyBinding
import com.yuchen.makeplan.ext.getVmFactory


class MembersItems : Fragment() {
    private val viewModel: MembersItemsViewModel by viewModels<MembersItemsViewModel> { getVmFactory(arguments?.getParcelable("project")?:MultiProject(),arguments?.getInt("object")?:0) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ItemMembersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = MembersItemsAdapter()
        binding.membersRecycler.adapter = adapter
        binding.membersRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitMembers(it)
            }
        })
        return binding.root
    }
}