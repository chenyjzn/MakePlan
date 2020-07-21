package com.yuchen.makeplan.members

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentMembersBinding

class MembersFragment : Fragment() {
    private lateinit var membersPagerAdapter: MembersPagerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding= FragmentMembersBinding.inflate(inflater, container, false)
        val pagerStringList = listOf("Members","Send","Receive")
        this.membersPagerAdapter = MembersPagerAdapter(childFragmentManager,pagerStringList, MembersFragmentArgs.fromBundle(requireArguments()).project)
        binding.membersTab.setupWithViewPager(binding.membersPager)
        binding.membersPager.adapter = membersPagerAdapter

        binding.membersAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    this.findNavController().navigate(MembersFragmentDirections.actionMembersFragmentToSearchUserFragment(MembersFragmentArgs.fromBundle(requireArguments()).project))
                    true
                }
                else -> false
            }
        }

        binding.membersAppBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }


        return binding.root
    }
}
