package com.yuchen.makeplan.notexist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuchen.makeplan.databinding.DialogNotExistCheckBinding
import com.yuchen.makeplan.ext.getVmFactory


class NotExistCheckDialog : DialogFragment() {

    private val viewModel: NotExistCheckViewModel by viewModels<NotExistCheckViewModel> { getVmFactory(NotExistCheckDialogArgs.fromBundle(requireArguments()).notExistProjects)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogNotExistCheckBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = NotExistCheckAdapter(viewModel)
        binding.notExistRecycler.adapter = adapter
        binding.notExistRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)

        viewModel.notExistProjects.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitProject(it)
            }
        })

        return binding.root
    }

}
