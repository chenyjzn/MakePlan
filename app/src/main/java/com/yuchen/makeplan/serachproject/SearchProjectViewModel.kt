package com.yuchen.makeplan.serachproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_RECEIVE_UID
import com.yuchen.makeplan.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchProjectViewModel(private val repository: MakePlanRepository) : ViewModel() {
    val projects: LiveData<List<MultiProject>> = repository.getAllMultiProjects()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var filterString = ""

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun requestUserToProject(project: MultiProject) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.requestUserToMultiProject(project, UserManager.user, FIELD_RECEIVE_UID)
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
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}