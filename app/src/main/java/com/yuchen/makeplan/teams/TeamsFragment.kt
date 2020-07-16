package com.yuchen.makeplan.teams

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentTeamsBinding
import com.yuchen.makeplan.ext.getVmFactory

class TeamsFragment : Fragment() {
    val viewModel by viewModels<TeamsViewModel> { getVmFactory() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTeamsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.teamAdd.setOnClickListener {
            viewModel.addTeamToFirebase("Test team")
        }

        viewModel.teams.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("chenyjzn","my teams = $it")
            }
        })

        return binding.root
    }
}
