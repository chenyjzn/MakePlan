package com.yuchen.makeplan.members

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
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

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    fun setUsersByUidList(uidList: List<String>) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.getUsersByUidList(uidList)
            when (result) {
                is Result.Success -> {
                    _users.value = result.data
                }
                is Result.Error -> {
                    _loadingStatus.value = LoadingStatus.ERROR("${result.exception}")
                }
                is Result.Fail -> {
                    _loadingStatus.value = LoadingStatus.ERROR(result.error)
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun cancelProjectToUser(user: User) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.cancelUserToMultiProject(project, user, FIELD_SEND_UID)
            when (result) {
                is Result.Success -> {

                }
                is Result.Error -> {
                    _loadingStatus.value = LoadingStatus.ERROR("${result.exception}")
                }
                is Result.Fail -> {
                    _loadingStatus.value = LoadingStatus.ERROR(result.error)
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun cancelUserToProject(user: User) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.cancelUserToMultiProject(project, user, FIELD_RECEIVE_UID)
            when (result) {
                is Result.Success -> {

                }
                is Result.Error -> {
                    _loadingStatus.value = LoadingStatus.ERROR("${result.exception}")
                }
                is Result.Fail -> {
                    _loadingStatus.value = LoadingStatus.ERROR(result.error)
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun acceptUserToProject(user: User) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.approveUserToMultiProject(project, user, FIELD_RECEIVE_UID)
            when (result) {
                is Result.Success -> {

                }
                is Result.Error -> {
                    _loadingStatus.value = LoadingStatus.ERROR("${result.exception}")
                }
                is Result.Fail -> {
                    _loadingStatus.value = LoadingStatus.ERROR(result.error)
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}