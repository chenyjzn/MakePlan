package com.yuchen.makeplan.searchuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SearchUserViewModel(private val repository: MakePlanRepository, private val project: Project) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val users: LiveData<List<User>> = repository.getUsersFromFirebase()

    fun inviteUserToProject(user: User){

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}