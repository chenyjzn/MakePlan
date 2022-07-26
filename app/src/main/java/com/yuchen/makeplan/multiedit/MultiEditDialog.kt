package com.yuchen.makeplan.multiedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.MainActivity
import com.yuchen.makeplan.databinding.DialogMultiEditBinding
import com.yuchen.makeplan.ext.getVmFactory

class MultiEditDialog : BottomSheetDialogFragment() {

    private val viewModel: MultiEditViewModel by viewModels { getVmFactory(MultiEditDialogArgs.fromBundle(requireArguments()).project) }
    lateinit var binding: DialogMultiEditBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogMultiEditBinding.inflate(inflater, container, false)

        binding.multiProjectRemoveButton.visibility = if (viewModel.project == null) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

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

        binding.multiProjectNameEdit.setText(viewModel.projectName.value)
        binding.multiProjectNameEdit.addTextChangedListener(
            onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                viewModel.projectName.value = text.toString()
            }
        )

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
        binding.multiProjectRemoveButton.isClickable = canTouch
        binding.multiProjectSaveButton.isClickable = canTouch
        this.isCancelable = canTouch
    }
}