package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.members.MembersItemsViewModel

class MembersItemsViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val project: MultiProject,
    private val membersPos: Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MembersItemsViewModel::class.java) ->
                    MembersItemsViewModel(makePlanRepository, project, membersPos)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}