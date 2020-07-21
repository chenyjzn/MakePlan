package com.yuchen.makeplan.searchuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_RECEIVE
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_SEND
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchUserViewModel(private val repository: MakePlanRepository, private val project: MultiProject) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val users: LiveData<List<User>> = repository.getAllUsers()

    fun inviteUserToProject(user: User){
        coroutineScope.launch {
            repository.requestUserToMultiProject(project,user,COLLECTION_SEND,COLLECTION_RECEIVE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}