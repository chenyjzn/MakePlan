package com.yuchen.makeplan.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class UsersViewModel(private val repository: MakePlanRepository,private val project: MultiProject) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val users: LiveData<List<User>> = repository.getMultiProjectUsersFromFirebase(project)

    fun removeProjectUser(user: User){

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}