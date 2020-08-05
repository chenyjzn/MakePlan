package com.yuchen.makeplan.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.MultiTask
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.multitask.MultiTaskViewModel

class MultiTaskViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val project: MultiProject,
    private val task: MultiTask?,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MultiTaskViewModel::class.java) ->
                    MultiTaskViewModel(makePlanRepository, project, task, application)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}