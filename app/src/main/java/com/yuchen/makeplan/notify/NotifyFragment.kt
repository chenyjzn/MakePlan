package com.yuchen.makeplan.notify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuchen.makeplan.databinding.FragmentNotifyBinding

class NotifyFragment : Fragment() {
    private lateinit var notifyPagerAdapter: NotifyPagerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentNotifyBinding.inflate(inflater, container, false)
        val pagerStringList = listOf("Send", "Receive")
        this.notifyPagerAdapter = NotifyPagerAdapter(childFragmentManager, pagerStringList)
        binding.notifyTab.setupWithViewPager(binding.notifyPager)
        binding.notifyPager.adapter = notifyPagerAdapter
        return binding.root
    }
}
