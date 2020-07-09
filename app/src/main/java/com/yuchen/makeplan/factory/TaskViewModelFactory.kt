package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.gantt.GanttViewModel
import com.yuchen.makeplan.task.TaskViewModel

class TaskViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val projectHistory : Array<Project>,
    private val pos : Int,
    private val colorList : List<String>
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(TaskViewModel::class.java)->
                    TaskViewModel(makePlanRepository, projectHistory,pos,colorList)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}