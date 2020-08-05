package com.yuchen.makeplan.members

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_MEMBERS_UID
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_RECEIVE_UID
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_SEND_UID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MembersItemsViewModel(private val repository: MakePlanRepository, private val project: MultiProject, val pagerPos: Int) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val usersUid: LiveData<List<String>> = when (pagerPos) {
        0 -> repository.getMultiProjectUsersUid(project, FIELD_MEMBERS_UID)
        1 -> repository.getMultiProjectUsersUid(project, FIELD_SEND_UID)
        2 -> repository.getMultiProjectUsersUid(project, FIELD_RECEIVE_UID)
        else -> throw IllegalArgumentException("Unknown pager position!!")
    }

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    fun setUsersByUidList(uidList: List<String>) {
        coroutineScope.launch {
            val result = repository.getUsersByUidList(uidList)
            when (result) {
                is Result.Success -> {
                    _users.value = result.data
                }
                is Result.Error -> {
                    Log.d("chenyjzn", "setUsersByUidList result = ${result.exception}")
                }
                is Result.Fail -> {
                    Log.d("chenyjzn", "setUsersByUidList result = ${result.error}")
                }
            }
        }
    }

    fun cancelProjectToUser(user: User) {
        coroutineScope.launch {
            repository.cancelUserToMultiProject(project, user, FIELD_SEND_UID)
        }
    }

    fun cancelUserToProject(user: User) {
        coroutineScope.launch {
            repository.cancelUserToMultiProject(project, user, FIELD_RECEIVE_UID)
        }
    }

    fun acceptUserToProject(user: User) {
        coroutineScope.launch {
            repository.approveUserToMultiProject(project, user, FIELD_RECEIVE_UID)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}