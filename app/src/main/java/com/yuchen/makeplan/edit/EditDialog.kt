package com.yuchen.makeplan.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuchen.makeplan.databinding.DialogEditBinding
import com.yuchen.makeplan.ext.getVmFactory

class EditDialog : BottomSheetDialogFragment() {

    private val viewModel: EditViewModel by viewModels<EditViewModel> { getVmFactory(EditDialogArgs.fromBundle(requireArguments()).project,EditDialogArgs.fromBundle(requireArguments()).isMultiProject)}

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

        viewModel.runDismiss.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.dismissDone()
                dismiss()
            }
        })
        return binding.root
    }
}
