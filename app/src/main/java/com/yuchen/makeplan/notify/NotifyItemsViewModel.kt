package com.yuchen.makeplan.notify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_RECEIVE_REQUEST
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_SEND_REQUEST
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_USERS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class NotifyItemsViewModel(private val repository: MakePlanRepository,val notifyPos: Int) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projects: LiveData<List<MultiProject>> = if (notifyPos == 0){
        repository.getMyMultiProjectsFromFirebase(COLLECTION_SEND_REQUEST)
    }else{
        repository.getMyMultiProjectsFromFirebase(COLLECTION_RECEIVE_REQUEST)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}