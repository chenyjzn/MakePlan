package com.yuchen.makeplan.multiedit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MultiEditViewModel(private val repository: MakePlanRepository, val project: MultiProject?) :
    ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projectName = MutableLiveData<String>().apply {
        value = project?.name ?: "Project"
    }

    private val _runDismiss = MutableLiveData<Boolean>()
    val runDismiss: LiveData<Boolean>
        get() = _runDismiss

    fun dismissDone() {
        _runDismiss.value = null
    }

    fun saveProject() {
        if (project == null) {
            val newProject = MultiProject(name = projectName.value ?: "Project")
            newProject.updateTime = System.currentTimeMillis()
            coroutineScope.launch {
                val result = repository.addMultiProject(newProject)
                when(result){
                    is com.yuchen.makeplan.Result.Success ->{

                    }
                    is com.yuchen.makeplan.Result.Error ->{
                        Log.d("chenyjzn", "getFireBaseUser result = ${result.exception}")
                    }
                    is com.yuchen.makeplan.Result.Fail ->{
                        Log.d("chenyjzn", "getFireBaseUser result = ${result.error}")
                    }
                }
                _runDismiss.value = true
            }
        } else {
            project.name = projectName.value ?: "Project"
            project.updateTime = System.currentTimeMillis()
            coroutineScope.launch {
                repository.updateMultiProject(project)
                _runDismiss.value = true
            }
        }

    }

    fun removeProject() {
        project?.let { project ->
            coroutineScope.launch {
                repository.removeMultiProject(project)
                _runDismiss.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}