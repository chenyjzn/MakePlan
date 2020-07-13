package com.yuchen.makeplan.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditViewModel (private val repository: MakePlanRepository, val project: Project?) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projectName = MutableLiveData<String>().apply {
        value = project?.name ?: "Project"
    }

    private val _runDismiss = MutableLiveData<Boolean>()
    val runDismiss: LiveData<Boolean>
        get() = _runDismiss

    fun dismissDone(){
        _runDismiss.value = null
    }

    fun saveProject(){
        if (project == null){
            val newProject = Project(name = projectName.value?:"Project")
            coroutineScope.launch {
                newProject.updateTime = System.currentTimeMillis()
                repository.insertProject(newProject)
                _runDismiss.value = true
            }
        }else{
            project.name = projectName.value?:"Project"
            coroutineScope.launch {
                project.updateTime = System.currentTimeMillis()
                repository.updateProject(project)
                _runDismiss.value = true
            }
        }
    }

    fun removeProject(){
        project?.let {project->
            coroutineScope.launch {
                repository.removeProject(project)
                _runDismiss.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}