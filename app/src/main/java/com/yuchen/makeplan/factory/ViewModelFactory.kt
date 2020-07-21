package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.MainViewModel
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.multiProjects.MultiProjectsViewModel
import com.yuchen.makeplan.projects.ProjectsViewModel
import com.yuchen.makeplan.search.SearchViewModel
import com.yuchen.makeplan.serachproject.SearchProjectViewModel

class ViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(makePlanRepository)
                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(makePlanRepository)
                isAssignableFrom(ProjectsViewModel::class.java) ->
                    ProjectsViewModel(makePlanRepository)
                isAssignableFrom(MultiProjectsViewModel::class.java) ->
                    MultiProjectsViewModel(makePlanRepository)
                isAssignableFrom(SearchProjectViewModel::class.java) ->
                    SearchProjectViewModel(makePlanRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}