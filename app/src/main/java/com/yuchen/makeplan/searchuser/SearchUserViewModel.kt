package com.yuchen.makeplan.searchuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.Result
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

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun requestProjectToUser(user: User) {
        coroutineScope.launch {
            val result = repository.requestUserToMultiProject(project, user, FIELD_SEND_UID)
            when(result){
                is Result.Success -> {

                }
                is Result.Error -> {
                    _loadingStatus.value = LoadingStatus.ERROR("${result.exception}")
                }
                is Result.Fail -> {
                    _loadingStatus.value = LoadingStatus.ERROR(result.error)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}