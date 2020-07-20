package com.yuchen.makeplan.multigantt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.*
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MultiGanttViewModel (private val repository: MakePlanRepository, private val projectInput : MultiProject) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val project: LiveData<MultiProject> = repository.getMultiProject(projectInput)
    val tasks:LiveData<List<MultiTask>> = repository.getMultiProjectTasks(projectInput)

    private val _taskSelect = MutableLiveData<MultiTask>()
    val taskSelect: LiveData<MultiTask>
        get() = _taskSelect

    fun setTaskSelect(task: MultiTask?){
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
                repository.removeMultiProjectTask(projectInput,task)
                _taskSelect.value = null
            }
        }
    }

    fun updateProjectCompleteRate(completeRate :Int){
        project.value?.let {project ->
            coroutineScope.launch {
                repository.updateMultiProjectCompleteRate(project,completeRate)
                _taskSelect.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}