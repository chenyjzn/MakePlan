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

    private val _navToGantt = MutableLiveData<Project>()
    val navToGantt: LiveData<Project>
        get() = _navToGantt

    private val _navToSetProject = MutableLiveData<Project>()
    val navToSetProject: LiveData<Project>
        get() = _navToSetProject

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    private val _notExistProjectsDownload = MutableLiveData<List<Project>>()
    val notExistProjectsDownload: LiveData<List<Project>>
        get() = _notExistProjectsDownload

    private val _notExistProjectsManage = MutableLiveData<List<Project>>()
    val notExistProjectsManage: LiveData<List<Project>>
        get() = _notExistProjectsManage

    private val _isFABCooling = MutableLiveData<Boolean>()
    val isFABCooling: LiveData<Boolean>
        get() = _isFABCooling

    fun navToGanttStart(project: Project) {
        _navToGantt.value = project
    }

    fun navToGanttDone() {
        _navToGantt.value = null
    }

    fun navToSetProjectStart(project: Project) {
        _navToSetProject.value = project
    }

    fun navToSetProjectDone() {
        _navToSetProject.value = null
    }

    fun resetNotExistProjectsDownload() {
        _notExistProjectsDownload.value = null
    }

    fun resetNotExistProjectsManage() {
        _notExistProjectsManage.value = null
    }

    fun uploadProjects() {
        coroutineScope.launch {
            projects.value?.let {
                if (it.isNotEmpty()) {
                    _loadingStatus.value = LoadingStatus.LOADING
                    val result = repository.uploadPersonalProjectsToFirebase(it)
                    when (result) {
                        is Result.Success -> {

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
    }

    fun searchAndDownloadProjects() {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.downloadPersonalProjectsFromFirebase()
            when (result) {
                is Result.Success -> {
                    var notExist : List<Project>? =null
                    result.data.forEach {
                        if (repository.searchProject(it.id)!= null){
                            repository.updateProject(it)
                        }else{
                            notExist = notExist.orEmpty() + listOf(it)
                        }
                    }
                    _notExistProjectsDownload.value = notExist
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

    fun manageProjects() {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.downloadPersonalProjectsFromFirebase()
            when (result) {
                is Result.Success -> {
                    var notExist : List<Project> = listOf()
                    result.data.forEach {
                        if (repository.searchProject(it.id)== null){
                            notExist = notExist + listOf(it)
                        }
                    }
                    _notExistProjectsManage.value = notExist
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

    fun resetProjects(projects: List<Project>, needRemove : BooleanArray){
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            for (i in 0..projects.lastIndex){
                if (needRemove[i]){
                    repository.removeProjectFromFirebase(projects[i].id)
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun downloadProjects(projects: List<Project>, needSave : BooleanArray){
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            for (i in 0..projects.lastIndex){
                if (needSave[i]){
                    repository.insertProject(projects[i])
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