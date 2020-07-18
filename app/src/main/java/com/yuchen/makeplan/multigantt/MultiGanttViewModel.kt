package com.yuchen.makeplan.multigantt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
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

    private val _projectSaveSuccess = MutableLiveData<Boolean>()
    val projectSaveSuccess: LiveData<Boolean>
        get() = _projectSaveSuccess

    private val _navigateToTaskSetting = MutableLiveData<Int>()
    val navigateToTaskSetting: LiveData<Int>
        get() = _navigateToTaskSetting

    private val _taskSelect = MutableLiveData<Int>().apply {
        value = -1
    }
    val taskSelect: LiveData<Int>
        get() = _taskSelect

    fun isTaskSelect():Int{
        return _taskSelect.value?:-1
    }

    fun setTaskSelect(taskPos: Int){
        if (_taskSelect.value != -1 && _taskSelect.value == taskPos)
            _taskSelect.value = -1
        else
            _taskSelect.value = taskPos
    }

    fun goToAddTask(){
        _navigateToTaskSetting.value = -1
    }

    fun goToEditTask(pos : Int){
        _navigateToTaskSetting.value = pos
    }

    fun goToTaskDone(){
        _navigateToTaskSetting.value = null
    }

    fun saveProjectDone(){
        _projectSaveSuccess.value = null
    }

    fun taskRemove(pos : Int){
        project.value?.let {
            val newProject = it.newRefProject()
            newProject.taskList.removeAt(pos)
            coroutineScope.launch {
                repository.updateProject(newProject)
                _taskSelect.value = -1
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}