package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.gantt.GanttViewModel
import com.yuchen.makeplan.task.TaskViewModel

class GanttViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val projectHistory : Array<Project>,
    private val pos : Int
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(GanttViewModel::class.java) ->
                    GanttViewModel(makePlanRepository, projectHistory,pos)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}