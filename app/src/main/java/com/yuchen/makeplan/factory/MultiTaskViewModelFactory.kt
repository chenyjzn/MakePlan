package com.yuchen.makeplan.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.multitask.MultiTaskViewModel
import com.yuchen.makeplan.task.TaskViewModel

class MultiTaskViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val project: Project,
    private val task: Task?,
    private val application : Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MultiTaskViewModel::class.java)->
                    MultiTaskViewModel(makePlanRepository,project,task,application)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}