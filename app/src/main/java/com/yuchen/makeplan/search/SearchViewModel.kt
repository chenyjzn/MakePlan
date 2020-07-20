package com.yuchen.makeplan.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_RECEIVE_REQUEST
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_SEND_REQUEST
import com.yuchen.makeplan.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel (private val repository: MakePlanRepository) : ViewModel(){
    val projects: LiveData<List<MultiProject>> = repository.getAllMultiProjects()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun sendJoinRequest(project: MultiProject){
        coroutineScope.launch {
            repository.requestUserToMultiProject(project,UserManager.user,COLLECTION_RECEIVE_REQUEST,COLLECTION_SEND_REQUEST)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}