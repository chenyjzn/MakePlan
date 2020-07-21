package com.yuchen.makeplan.multi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuchen.makeplan.databinding.ItemMultiBinding
import com.yuchen.makeplan.ext.getVmFactory


class MultiItems : Fragment() {
    private val viewModel: MultiItemsViewModel by viewModels<MultiItemsViewModel> { getVmFactory(arguments?.getInt("object")?:0) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ItemMultiBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = MultiItemsAdapter()
        binding.itemMultiRecycler.adapter = adapter
        binding.itemMultiRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}