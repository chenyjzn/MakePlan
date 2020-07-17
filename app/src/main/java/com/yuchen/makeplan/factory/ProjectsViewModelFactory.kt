package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.MainViewModel
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.projects.ProjectsViewModel

class ProjectsViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val isMultiProject : Boolean
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ProjectsViewModel::class.java) ->
                    ProjectsViewModel(makePlanRepository,isMultiProject)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}