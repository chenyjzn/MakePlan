package com.yuchen.makeplan.multiProjects

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_MEMBERS_UID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MultiProjectsViewModel(private val repository: MakePlanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projects: LiveData<List<MultiProject>> = repository.getMyMultiProjects(FIELD_MEMBERS_UID)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}