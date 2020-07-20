package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.inviteusers.InviteUsersViewModel
import com.yuchen.makeplan.joinuser.JoinUserViewModel
import com.yuchen.makeplan.multigantt.MultiGanttViewModel
import com.yuchen.makeplan.searchuser.SearchUserViewModel
import com.yuchen.makeplan.users.UsersViewModel

class MultiGanttViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val project : MultiProject
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MultiGanttViewModel::class.java) ->
                    MultiGanttViewModel(makePlanRepository, project)
                isAssignableFrom(UsersViewModel::class.java) ->
                    UsersViewModel(makePlanRepository, project)
                isAssignableFrom(SearchUserViewModel::class.java) ->
                    SearchUserViewModel(makePlanRepository, project)
                isAssignableFrom(JoinUserViewModel::class.java) ->
                    JoinUserViewModel(makePlanRepository, project)
                isAssignableFrom(InviteUsersViewModel::class.java) ->
                    InviteUsersViewModel(makePlanRepository, project)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}