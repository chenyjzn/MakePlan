package com.yuchen.makeplan.multi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        return binding.root
    }
}
