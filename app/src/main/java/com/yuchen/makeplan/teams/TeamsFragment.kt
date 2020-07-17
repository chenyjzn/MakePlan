package com.yuchen.makeplan.teams

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuchen.makeplan.databinding.FragmentTeamsBinding
import com.yuchen.makeplan.ext.getVmFactory

class TeamsFragment : Fragment() {
    val viewModel by viewModels<TeamsViewModel> { getVmFactory() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTeamsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = TeamsAdapter(viewModel)
        binding.teamsRecycler.adapter = adapter
        binding.teamsRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.teamAdd.setOnClickListener {
            viewModel.addTeamToFirebase("Test3 team")
        }

        viewModel.myTeams.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitTeams(it)
            }
        })

        viewModel.searchTeams.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitTeams(it)
            }
        })

        binding.teamsSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.findTeamsByText(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.teamsSearchView.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){

            }
        }
        return binding.root
    }
}
