package com.yuchen.makeplan.notify

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class NotifyPagerAdapter(fm: FragmentManager, val pagerList:List<String>) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int  = 2

    override fun getItem(i: Int): Fragment {
        val fragment = NotifyItems()
        fragment.arguments = Bundle().apply {
            putInt("object", i)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when(position){
            0-> pagerList[0]
            1-> pagerList[1]
            else -> throw IllegalAccessError("Out of pager pos")
        }
    }

}