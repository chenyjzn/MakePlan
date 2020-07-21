package com.yuchen.makeplan.notify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_RECEIVE
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_SEND
import com.yuchen.makeplan.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotifyItemsViewModel(private val repository: MakePlanRepository,val notifyPos: Int) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projects: LiveData<List<MultiProject>> = if (notifyPos == 0){
        repository.getMyMultiProjects(COLLECTION_SEND)
    }else{
        repository.getMyMultiProjects(COLLECTION_RECEIVE)
    }

    fun confirmInvite(project: MultiProject){
        coroutineScope.launch {
            repository.approveUserToMultiProject(project,UserManager.user, COLLECTION_SEND,COLLECTION_RECEIVE)
        }
    }

    fun cancelSend(project: MultiProject){
        coroutineScope.launch {
            repository.cancelUserToMultiProject(project,UserManager.user,COLLECTION_RECEIVE,COLLECTION_SEND)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}