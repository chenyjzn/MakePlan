package com.yuchen.makeplan.joinuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_RECEIVE_REQUEST
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_SEND_REQUEST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class JoinUserViewModel(private val repository: MakePlanRepository, private val project: MultiProject) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val users: LiveData<List<User>> = repository.getMultiProjectUsers(project,COLLECTION_RECEIVE_REQUEST)

    fun confirmUserJoin(user: User){
        coroutineScope.launch {
            repository.approveUserToMultiProject(project,user,COLLECTION_RECEIVE_REQUEST,COLLECTION_SEND_REQUEST)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}