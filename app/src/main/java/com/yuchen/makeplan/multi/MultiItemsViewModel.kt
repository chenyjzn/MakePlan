package com.yuchen.makeplan.multi

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_MEMBERS_UID
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_RECEIVE_UID
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_SEND_UID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MultiItemsViewModel(private val repository: MakePlanRepository, val pagerPos: Int) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projects: LiveData<List<MultiProject>> = when (pagerPos){
        0 -> repository.getMyMultiProjects(FIELD_MEMBERS_UID)
        1 -> repository.getMyMultiProjects(FIELD_RECEIVE_UID)
        2 -> repository.getMyMultiProjects(FIELD_SEND_UID)
        else -> throw IllegalArgumentException("Unknown pager position!!")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}