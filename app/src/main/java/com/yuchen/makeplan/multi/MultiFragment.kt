package com.yuchen.makeplan.multi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentMultiBinding

class MultiFragment : Fragment() {
    private lateinit var multiPagerAdapter: MultiPagerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding= FragmentMultiBinding.inflate(inflater, container, false)
        val pagerStringList = listOf("Projects","Sent","Receive")
        this.multiPagerAdapter = MultiPagerAdapter(childFragmentManager,pagerStringList)
        binding.multiTab.setupWithViewPager(binding.multiPager)
        binding.multiPager.adapter = multiPagerAdapter
        binding.multiAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.search_project -> {
                    this.findNavController().navigate(MultiFragmentDirections.actionMultiFragmentToSearchProjectFragment())
                    true
                }
                R.id.add_project-> {
                    this.findNavController().navigate(MultiFragmentDirections.actionMultiFragmentToMultiEditDialog(null))
                    true
                }
                else -> false
            }
        }
        return binding.root
    }
}
