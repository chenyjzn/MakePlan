package com.yuchen.makeplan.login

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuchen.makeplan.MainActivity
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.DialogLoginBinding

class LoginDialog : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogLoginBinding.inflate(inflater, container, false)
        binding.loginButton.setOnClickListener {
            (activity as MainActivity).signIn()
            dismiss()
        }
        return binding.root
    }

}
