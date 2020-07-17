package com.yuchen.makeplan.projects

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
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.FragmentProjectsBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.util.UserManager

class ProjectsFragment : Fragment() {


    private val viewModel: ProjectsViewModel by viewModels<ProjectsViewModel> { getVmFactory(ProjectsFragmentArgs.fromBundle(requireArguments()).isMultiProject) }
    lateinit var binding: FragmentProjectsBinding

    fun setProjectsFragmentFun(isMultiProject : Boolean){
        if (isMultiProject){
            binding.projectsUpload.visibility = View.GONE
            binding.projectsDownload.visibility = View.GONE
        }else {
            binding.projectsMultiSearch.visibility = View.GONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProjectsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val projectsAdapter = ProjectsAdapter()
        projectsAdapter.setItemClickListener(object : ProjectsAdapter.OnClickListener{
            override fun onProjectClick(project: Project) {
                viewModel.goToGantt(project)
            }
            override fun onProjectLongClick(project: Project) {
                viewModel.goToProjectSetting(project)
            }
        })

        binding.projectsRecycler.adapter = projectsAdapter

        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                projectsAdapter.submitProjects(it)
            }
        })
        viewModel.navigateToGantt.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToGanttFragment(arrayOf(it),0,viewModel.isMultiProject))
                viewModel.goToGanttDone()
            }
        })

        viewModel.navigateToProjectSetting.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToEditDialog(it,viewModel.isMultiProject))
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
            this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToEditDialog(null,viewModel.isMultiProject))
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

        binding.projectsMultiSearch.setOnClickListener {
            this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToSearchFragment())
        }

        setProjectsFragmentFun(viewModel.isMultiProject)

        return binding.root
    }
}
