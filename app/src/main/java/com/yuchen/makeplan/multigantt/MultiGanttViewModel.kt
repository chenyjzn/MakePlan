package com.yuchen.makeplan.multigantt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.ext.removeFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MultiGanttViewModel (private val repository: MakePlanRepository, private val projectInput : Project) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val project: LiveData<Project> = repository.getMultiProjectFromFirebase(projectInput)
    val tasks:LiveData<List<Task>> = repository.getMultiProjectTasksFromFirebase(projectInput)

    private val _taskSelect = MutableLiveData<Task>()
    val taskSelect: LiveData<Task>
        get() = _taskSelect

    fun setTaskSelect(task: Task?){
        if (_taskSelect.value != null && _taskSelect.value == task)
            _taskSelect.value = null
        else
            _taskSelect.value = task
    }

    fun taskSelectClear(){
        _taskSelect.value = null
    }

    fun taskRemove(){
        _taskSelect.value?.let {task ->
            coroutineScope.launch {
                repository.removeMultiProjectTaskFromFirebase(projectInput,task)
                _taskSelect.value = null
            }
        }
    }

    fun updateProjectCompleteRate(completeRate :Int){
        project.value?.let {project ->
            coroutineScope.launch {
                repository.updateMultiProjectCompleteRateToFirebase(project,completeRate)
                _taskSelect.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}