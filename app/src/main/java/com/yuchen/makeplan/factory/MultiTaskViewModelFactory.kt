package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.multitask.MultiTaskViewModel
import com.yuchen.makeplan.task.TaskViewModel

class MultiTaskViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val project : Project,
    private val taskPos : Int,
    private val colorList : List<String>
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MultiTaskViewModel::class.java)->
                    MultiTaskViewModel(makePlanRepository, project,taskPos,colorList)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}