package com.yuchen.makeplan.searchuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_SEND_UID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchUserViewModel(
    private val repository: MakePlanRepository,
    private val project: MultiProject
) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val myProject: LiveData<MultiProject> = repository.getMultiProject(project)
    val users: LiveData<List<User>> = repository.getAllUsers()

    var filterString = ""

    fun requestProjectToUser(user: User) {
        coroutineScope.launch {
            repository.requestUserToMultiProject(project, user, FIELD_SEND_UID)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}