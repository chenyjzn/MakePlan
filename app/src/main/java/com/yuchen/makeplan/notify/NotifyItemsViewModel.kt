package com.yuchen.makeplan.notify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource
import com.yuchen.makeplan.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotifyItemsViewModel(private val repository: MakePlanRepository, val pagerPos: Int) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projects: LiveData<List<MultiProject>> = when (pagerPos) {
        0 -> repository.getMyMultiProjects(MakePlanRemoteDataSource.FIELD_RECEIVE_UID)
        1 -> repository.getMyMultiProjects(MakePlanRemoteDataSource.FIELD_SEND_UID)
        else -> throw IllegalArgumentException("Unknown pager position!!")
    }

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun cancelUserToProject(project: MultiProject) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.cancelUserToMultiProject(project, UserManager.user, MakePlanRemoteDataSource.FIELD_RECEIVE_UID)
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

    fun acceptProjectToUser(project: MultiProject) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.approveUserToMultiProject(project, UserManager.user, MakePlanRemoteDataSource.FIELD_SEND_UID)
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

    fun cancelProjectToUser(project: MultiProject) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.cancelUserToMultiProject(project, UserManager.user, MakePlanRemoteDataSource.FIELD_SEND_UID)
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