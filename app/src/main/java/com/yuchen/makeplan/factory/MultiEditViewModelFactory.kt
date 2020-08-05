package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.multiedit.MultiEditViewModel

class MultiEditViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val project: MultiProject?
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MultiEditViewModel::class.java) ->
                    MultiEditViewModel(makePlanRepository, project)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}