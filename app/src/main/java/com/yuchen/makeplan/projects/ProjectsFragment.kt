package com.yuchen.makeplan.projects

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuchen.makeplan.BUTTON_CLICK_TRAN
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.NavigationDirections
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.databinding.FragmentProjectsBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.util.UserManager

class ProjectsFragment : Fragment() {


    private val viewModel: ProjectsViewModel by viewModels<ProjectsViewModel> { getVmFactory() }
    lateinit var binding: FragmentProjectsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProjectsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val projectsAdapter = ProjectsAdapter(viewModel)
        projectsAdapter.setItemClickListener(object : ProjectsAdapter.OnClickListener{
            override fun onProjectClick(project: Project) {
                viewModel.navToGanttStart(project)
            }
            override fun onProjectLongClick(project: Project) {
                viewModel.navToSetProjectStart(project)
            }
        })

        binding.projectsRecycler.adapter = projectsAdapter

        viewModel.projects.observe(viewLifecycleOwner, Observer {
            it?.let {
                projectsAdapter.submitList(it)
                projectsAdapter.notifyDataSetChanged()
            }
        })
        viewModel.navToGantt.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToGanttFragment(arrayOf(it),0))
                viewModel.navToGanttDone()
            }
        })

        viewModel.navToSetProject.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToEditDialog(it))
                viewModel.navToSetProjectDone()
            }
        })

        viewModel.notExistProjectsDownload.observe(viewLifecycleOwner, Observer {
            it?.let {
                val multiItems = it.map {
                    it.name
                }.toTypedArray()
                val checkedItems = it.map {
                    false
                }.toBooleanArray()
                MaterialAlertDialogBuilder(requireNotNull(context))
                    .setTitle("Do you want to download this projects?")
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setMultiChoiceItems(multiItems, checkedItems) { dialog, which, checked ->

                    }.setPositiveButton("Save") { dialog, which ->
                        viewModel.downloadProjects(it,checkedItems)
                    }
                    .show()
                viewModel.resetNotExistProjectsDownload()
            }
        })

        viewModel.notExistProjectsManage.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isEmpty()){
                    MaterialAlertDialogBuilder(requireNotNull(context))
                        .setTitle("Cloud do not have extra project")
                        .setPositiveButton("Yes") { dialog, which ->
                        }
                        .show()
                }else{
                    val multiItems = it.map {
                        it.name
                    }.toTypedArray()
                    val checkedItems = it.map {
                        false
                    }.toBooleanArray()
                    MaterialAlertDialogBuilder(requireNotNull(context))
                        .setTitle("Do you want to remove this projects from cloud?")
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setMultiChoiceItems(multiItems, checkedItems) { dialog, which, checked ->

                        }.setPositiveButton("Yes") { dialog, which ->
                            viewModel.resetProjects(it,checkedItems)
                        }
                        .show()
                }
                viewModel.resetNotExistProjectsManage()
            }
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    LoadingStatus.LOADING ->{
                        binding.projectsProgress.visibility = View.VISIBLE
                        binding.projectsAddProject.isClickable = false
                        binding.projectsAppBar.menu.findItem(R.id.cloud_download).isEnabled = false
                        binding.projectsAppBar.menu.findItem(R.id.cloud_manage).isEnabled = false
                        binding.projectsAppBar.menu.findItem(R.id.cloud_upload).isEnabled = false
                    }
                    LoadingStatus.DONE -> {
                        binding.projectsProgress.visibility = View.INVISIBLE
                        binding.projectsAddProject.isClickable = true
                        binding.projectsAppBar.menu.findItem(R.id.cloud_download).isEnabled = true
                        binding.projectsAppBar.menu.findItem(R.id.cloud_manage).isEnabled = true
                        binding.projectsAppBar.menu.findItem(R.id.cloud_upload).isEnabled = true
                    }
                    LoadingStatus.ERROR -> {
                        binding.projectsProgress.visibility = View.INVISIBLE
                        binding.projectsAddProject.isClickable = true
                        binding.projectsAppBar.menu.findItem(R.id.cloud_download).isEnabled = true
                        binding.projectsAppBar.menu.findItem(R.id.cloud_manage).isEnabled = true
                        binding.projectsAppBar.menu.findItem(R.id.cloud_upload).isEnabled = true
                    }
                }
            }
        })

        binding.projectsAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cloud_download -> {
                    if (UserManager.isLogIn()){
                        viewModel.searchAndDownloadProjects()
                    }
                    else{
                        this.findNavController().navigate(NavigationDirections.actionGlobalLoginDialog())
                        menuItem.isEnabled = false
                        Handler().postDelayed({ menuItem.isEnabled = true}, BUTTON_CLICK_TRAN)
                    }
                    true
                }
                R.id.cloud_manage -> {
                    if (UserManager.isLogIn()){
                        viewModel.manageProjects()
                    }
                    else{
                        this.findNavController().navigate(NavigationDirections.actionGlobalLoginDialog())
                        menuItem.isEnabled = false
                        Handler().postDelayed({ menuItem.isEnabled = true}, BUTTON_CLICK_TRAN)
                    }
                    true
                }
                R.id.cloud_upload -> {
                    if (UserManager.isLogIn()){
                        viewModel.uploadProjects()
                    }
                    else{
                        this.findNavController().navigate(NavigationDirections.actionGlobalLoginDialog())
                        menuItem.isEnabled = false
                        Handler().postDelayed({ menuItem.isEnabled = true}, BUTTON_CLICK_TRAN)
                    }
                    true
                }
                else -> false
            }
        }

        binding.projectsAddProject.setOnClickListener {
            this.findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToEditDialog(null))
            binding.projectsAddProject.isClickable = false
            Handler().postDelayed({ binding.projectsAddProject.isClickable = true}, BUTTON_CLICK_TRAN)
        }
        return binding.root
    }
}
