package com.yuchen.makeplan.multiedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.MainActivity
import com.yuchen.makeplan.databinding.DialogMultiEditBinding
import com.yuchen.makeplan.ext.getVmFactory

class MultiEditDialog : BottomSheetDialogFragment() {

    private val viewModel: MultiEditViewModel by viewModels<MultiEditViewModel> { getVmFactory(MultiEditDialogArgs.fromBundle(requireArguments()).project) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogMultiEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.multiProjectRemoveButton.setOnClickListener {
            viewModel.liveProject.value?.let {
                if (it.members.size > 1) {
                    viewModel.leaveProject()
                } else {
                    viewModel.removeProject()
                }
            }
        }

        binding.multiProjectSaveButton.setOnClickListener {
            viewModel.saveProject()
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoadingStatus.LOADING -> {
                    (activity as MainActivity).showProgress()
                    binding.multiProjectRemoveButton.isClickable = false
                    binding.multiProjectSaveButton.isClickable = false
                    this.isCancelable = false
                }
                LoadingStatus.DONE -> {
                    (activity as MainActivity).hideProgress()
                    binding.multiProjectRemoveButton.isClickable = true
                    binding.multiProjectSaveButton.isClickable = true
                    this.isCancelable = true
                }
                LoadingStatus.ERROR -> {
                    (activity as MainActivity).hideProgress()
                    binding.multiProjectRemoveButton.isClickable = true
                    binding.multiProjectSaveButton.isClickable = true
                    this.isCancelable = true
                }
            }
        })

        viewModel.runDismiss.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.dismissDone()
                dismiss()
            }
        })
        return binding.root
    }
}