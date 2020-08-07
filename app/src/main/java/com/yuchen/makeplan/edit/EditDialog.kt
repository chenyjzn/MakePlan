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

    private val viewModel: EditViewModel by viewModels { getVmFactory(EditDialogArgs.fromBundle(requireArguments()).project) }
    lateinit var binding: DialogEditBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogEditBinding.inflate(inflater, container, false)
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
                is LoadingStatus.LOADING -> {
                    (activity as MainActivity).showProgress()
                    isTouchable(false)
                }
                is LoadingStatus.DONE -> {
                    (activity as MainActivity).hideProgress()
                    isTouchable(true)
                }
                is LoadingStatus.ERROR -> {
                    (activity as MainActivity).showErrorMessage(it.message)
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

    private fun isTouchable(canTouch: Boolean) {
        binding.projectRemoveButton.isClickable = canTouch
        binding.projectSaveButton.isClickable = canTouch
        this.isCancelable = canTouch
    }
}
