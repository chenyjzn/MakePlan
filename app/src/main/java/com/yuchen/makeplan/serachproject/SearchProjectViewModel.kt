package com.yuchen.makeplan.serachproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_RECEIVE_UID
import com.yuchen.makeplan.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchProjectViewModel (private val repository: MakePlanRepository) : ViewModel(){
    val projects: LiveData<List<MultiProject>> = repository.getAllMultiProjects()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun requestUserToProject(project: MultiProject){
        coroutineScope.launch {
            repository.requestUserToMultiProject(project,UserManager.user, FIELD_RECEIVE_UID)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}