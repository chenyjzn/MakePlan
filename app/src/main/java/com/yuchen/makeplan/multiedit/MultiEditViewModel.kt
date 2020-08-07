package com.yuchen.makeplan.multiedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MultiEditViewModel(private val repository: MakePlanRepository, val project: MultiProject?) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var liveProject: LiveData<MultiProject> = if (project != null) {
        repository.getMultiProject(project)
    } else {
        MutableLiveData()
    }

    val projectName = MutableLiveData<String>().apply {
        value = project?.name ?: "Project"
    }

    private val _runDismiss = MutableLiveData<Boolean>()
    val runDismiss: LiveData<Boolean>
        get() = _runDismiss

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun dismissDone() {
        _runDismiss.value = null
    }

    fun saveProject() {
        if (project == null) {
            val newProject = MultiProject(name = projectName.value ?: "Project")
            newProject.updateTime = System.currentTimeMillis()
            coroutineScope.launch {
                _loadingStatus.value = LoadingStatus.LOADING
                val result = repository.addMultiProject(newProject)
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
                _runDismiss.value = true
            }
        } else {
            project.name = projectName.value ?: "Project"
            project.updateTime = System.currentTimeMillis()
            coroutineScope.launch {
                _loadingStatus.value = LoadingStatus.LOADING
                val result = repository.updateMultiProject(project)
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
                _runDismiss.value = true
            }
        }

    }

    fun leaveProject() {
        project?.let { project ->
            coroutineScope.launch {
                _loadingStatus.value = LoadingStatus.LOADING
                val result = repository.removeUserToMultiProject(project, UserManager.user)
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
                _runDismiss.value = true
            }
        }
    }

    fun removeProject() {
        project?.let { project ->
            coroutineScope.launch {
                _loadingStatus.value = LoadingStatus.LOADING
                val result = repository.removeMultiProject(project)
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
                _runDismiss.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}