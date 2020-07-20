package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.edit.EditViewModel

class EditViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val project: Project?
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(EditViewModel::class.java) ->
                    EditViewModel(makePlanRepository,project)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}