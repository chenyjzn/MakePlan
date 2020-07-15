package com.yuchen.makeplan.projects

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProjectsViewModel(private val repository: MakePlanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projects: LiveData<List<Project>> = repository.getAllProjects()

    private val _navigateToGantt = MutableLiveData<Project>()
    val navigateToGantt: LiveData<Project>
        get() = _navigateToGantt

    private val _navigateToProjectSetting = MutableLiveData<Project>()
    val navigateToProjectSetting: LiveData<Project>
        get() = _navigateToProjectSetting

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    private val _notExistProjects = MutableLiveData<List<Project>>()
    val notExistProjects: LiveData<List<Project>>
        get() = _notExistProjects

    fun goToGantt(project: Project) {
        _navigateToGantt.value = project
    }

    fun goToGanttDone() {
        _navigateToGantt.value = null
    }

    fun goToProjectSetting(project: Project?) {
        _navigateToProjectSetting.value = project
    }

    fun goToProjectSettingDone() {
        _navigateToProjectSetting.value = null
    }

    fun resetProjectsDone() {
        _notExistProjects.value = null
    }

    fun uploadProjects() {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            projects.value?.let {
                val result = repository.uploadPersonalProjectsToFirebase(it)
                when (result) {
                    is Result.Success -> {
                        Log.d("chenyjzn", "uploadProjects OK")
                    }
                    is Result.Error -> {
                        Log.d("chenyjzn", "uploadProjects result = ${result.exception}")
                    }
                    is Result.Fail -> {
                        Log.d("chenyjzn", "uploadProjects result = ${result.error}")
                    }
                }
                _loadingStatus.value = LoadingStatus.DONE
            }
        }
    }

    fun downloadProjects() {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.downloadPersonalProjectsFromFirebase()
            when (result) {
                is Result.Success -> {
                    Log.d("chenyjzn", "downloadProjects OK")
                    var notExist : List<Project>? =null
                    result.data.forEach {
                        if (repository.searchProject(it.id)!= null){
                            repository.updateProject(it)
                        }else{
                            notExist = notExist.orEmpty() + listOf(it)
                        }
                    }
                    _notExistProjects.value = notExist
                }
                is Result.Error -> {
                    Log.d("chenyjzn", "downloadProjects result = ${result.exception}")
                }
                is Result.Fail -> {
                    Log.d("chenyjzn", "downloadProjects result = ${result.error}")
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun resetProjects(projects: List<Project>, needSave : BooleanArray){
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            for (i in 0..projects.lastIndex){
                if (needSave[i]){
                    repository.insertProject(projects[i])
                }else{
                    repository.removeProjectFromFirebase(projects[i].id)
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