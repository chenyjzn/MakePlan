package com.yuchen.makeplan.projects

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.MainActivity
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentProjectsBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.util.UserManager

class ProjectsFragment : Fragment() {

    private val viewModel: ProjectsViewModel by viewModels<ProjectsViewModel> { getVmFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentProjectsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val projectsAdapter = ProjectsAdapter(viewModel)

        binding.projectsRecycler.adapter = projectsAdapter

        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                projectsAdapter.submitProjects(it)
            }
        })

        viewModel.navigateToGantt.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToGanttFragment(arrayOf(it),0))
                viewModel.goToGanttDone()
            }
        })

        viewModel.navigateToProjectSetting.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToEditDialog(it))
                viewModel.goToProjectSettingDone()
            }
        })

        viewModel.notExistProjects.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("chenyjzn","not exist : $it")
                val multiItems = it.map {
                    it.name
                }.toTypedArray()
                val checkedItems = it.map {
                    false
                }.toBooleanArray()
                MaterialAlertDialogBuilder(requireNotNull(context))
                    .setTitle("Projects not exist !!")
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setMultiChoiceItems(multiItems, checkedItems) { dialog, which, checked ->

                    }.setPositiveButton("Save") { dialog, which ->
                        viewModel.resetProjects(it,checkedItems)
                    }
                    .show()
                viewModel.resetProjectsDone()
            }
        })

        binding.projectAdd.setOnClickListener {
            this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToEditDialog(null))
        }

        binding.projectsUpload.setOnClickListener {
            if (UserManager.isLogIn()){
                viewModel.uploadProjects()
            }
            else{
                val view = binding.projectsBackGround
                Snackbar.make(view, "Please login", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Google Login"){
                        (activity as MainActivity).signIn()
                    }
                    .show()
            }
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    LoadingStatus.LOADING ->{
                        binding.projectsProgress.visibility = View.VISIBLE
                    }
                    LoadingStatus.DONE -> {
                        binding.projectsProgress.visibility = View.INVISIBLE
                    }
                    LoadingStatus.ERROR -> {

                    }
                }
            }
        })

        binding.projectsDownload.setOnClickListener {
            if (UserManager.isLogIn()){
                viewModel.downloadProjects()
            }
            else{
                val view = binding.projectsBackGround
                Snackbar.make(view, "Please login", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Google Login"){
                        (activity as MainActivity).signIn()
                    }
                    .show()
            }
        }

        return binding.root
    }
}
