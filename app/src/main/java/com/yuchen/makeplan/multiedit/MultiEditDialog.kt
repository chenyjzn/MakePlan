package com.yuchen.makeplan.multiedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuchen.makeplan.databinding.DialogMultiEditBinding
import com.yuchen.makeplan.ext.getVmFactory

class MultiEditDialog : BottomSheetDialogFragment() {

    private val viewModel: MultiEditViewModel by viewModels<MultiEditViewModel> { getVmFactory(MultiEditDialogArgs.fromBundle(requireArguments()).project)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogMultiEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.multiProjectRemoveButton.setOnClickListener {
            viewModel.removeProject()
        }

        binding.multiProjectSaveButton.setOnClickListener {
            viewModel.saveProject()
        }

        viewModel.runDismiss.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.dismissDone()
                dismiss()
            }
        })
        return binding.root
    }
}