package com.yuchen.makeplan.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.yuchen.makeplan.databinding.FragmentSearchBinding
import com.yuchen.makeplan.ext.getVmFactory

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels<SearchViewModel> { getVmFactory() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.searchRecycler.adapter
        viewModel.projects.observe(viewLifecycleOwner, Observer {

        })
        return binding.root
    }
}
