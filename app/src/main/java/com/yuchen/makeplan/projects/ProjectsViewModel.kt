package com.yuchen.makeplan.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProjectsViewModel (private val repository: MakePlanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projects: LiveData<List<Project>> = repository.getAllProjects()

    private val _navigateToGantt = MutableLiveData<Project>()
    val navigateToGantt: LiveData<Project>
        get() = _navigateToGantt

    private val _navigateToProjectSetting = MutableLiveData<Project>()
    val navigateToProjectSetting: LiveData<Project>
        get() = _navigateToProjectSetting

    fun goToGantt(project: Project){
        _navigateToGantt.value = project
    }

    fun goToGanttDone(){
        _navigateToGantt.value = null
    }

    fun goToProjectSetting(project: Project?){
        _navigateToProjectSetting.value = project
    }

    fun goToProjectSettingDone(){
        _navigateToProjectSetting.value = null
    }

    fun addProject(){
        coroutineScope.launch {
            repository.insertProject(Project())
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}