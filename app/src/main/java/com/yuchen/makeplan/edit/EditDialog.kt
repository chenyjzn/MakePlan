package com.yuchen.makeplan.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.MainActivity
import com.yuchen.makeplan.databinding.DialogEditBinding
import com.yuchen.makeplan.ext.getVmFactory

class EditDialog : BottomSheetDialogFragment() {
    private val viewModel: EditViewModel by viewModels<EditViewModel> { getVmFactory(EditDialogArgs.fromBundle(requireArguments()).project) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.projectRemoveButton.setOnClickListener {
            viewModel.removeProject()
        }

        binding.projectSaveButton.setOnClickListener {
            viewModel.saveProject()
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoadingStatus.LOADING -> {
                    (activity as MainActivity).showProgress()
                    binding.projectRemoveButton.isClickable = false
                    binding.projectSaveButton.isClickable = false
                    this.isCancelable = false
                }
                LoadingStatus.DONE -> {
                    (activity as MainActivity).hideProgress()
                    binding.projectRemoveButton.isClickable = false
                    binding.projectSaveButton.isClickable = false
                    this.isCancelable = true
                }
                LoadingStatus.ERROR -> {
                    (activity as MainActivity).hideProgress()
                    binding.projectRemoveButton.isClickable = false
                    binding.projectSaveButton.isClickable = false
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
