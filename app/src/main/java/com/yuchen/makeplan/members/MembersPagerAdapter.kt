package com.yuchen.makeplan.members

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.yuchen.makeplan.data.MultiProject

class MembersPagerAdapter(fm: FragmentManager, val pagerList:List<String>, val project: MultiProject) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int  = 3

    override fun getItem(i: Int): Fragment {
        val fragment = MembersItems()
        fragment.arguments = Bundle().apply {
            putInt("object", i)
            putParcelable("project",project)
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