package com.yuchen.makeplan.multi

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MultiPagerAdapter(fm: FragmentManager, val pagerList:List<String>) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int  = 3

    override fun getItem(i: Int): Fragment {
        val fragment = MultiItems()
        fragment.arguments = Bundle().apply {
            putInt("object", i)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when(position){
            0-> pagerList[0]
            1-> pagerList[1]
            2-> pagerList[2]
            else -> throw IllegalAccessError("Out of pager pos")
        }
    }

}