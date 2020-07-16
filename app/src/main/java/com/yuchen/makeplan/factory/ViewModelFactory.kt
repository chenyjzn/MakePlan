package com.yuchen.makeplan.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.MainViewModel
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.gantt.GanttViewModel
import com.yuchen.makeplan.projects.ProjectsViewModel
import com.yuchen.makeplan.teams.TeamsViewModel

class ViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ProjectsViewModel::class.java) ->
                    ProjectsViewModel(makePlanRepository)
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(makePlanRepository)
                isAssignableFrom(TeamsViewModel::class.java) ->
                    TeamsViewModel(makePlanRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}