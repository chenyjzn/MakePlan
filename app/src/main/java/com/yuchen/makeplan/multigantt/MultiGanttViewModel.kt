package com.yuchen.makeplan.multigantt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.data.*
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MultiGanttViewModel(private val repository: MakePlanRepository, private val projectInput: MultiProject) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val project: LiveData<MultiProject> = repository.getMultiProject(projectInput)
    val tasks: LiveData<List<MultiTask>> = repository.getMultiProjectTasks(projectInput)

    private val _taskSelect = MutableLiveData<MultiTask>()
    val taskSelect: LiveData<MultiTask>
        get() = _taskSelect

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    private val _taskTimeScale = MutableLiveData<Int>().apply {
        value = 0
    }
    val taskTimeScale: LiveData<Int>
        get() = _taskTimeScale

    fun setTaskTimeScale(time: Int) {
        _taskTimeScale.value = time
    }

    fun setTaskSelect(task: MultiTask?) {
        if (_taskSelect.value != null && _taskSelect.value == task)
            _taskSelect.value = null
        else
            _taskSelect.value = task
    }

    fun updateTaskToFirebase(task: MultiTask) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            repository.updateMultiProjectTask(projectInput, task)
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun taskSelectClear() {
        _taskSelect.value = null
    }

    fun taskRemove() {
        _taskSelect.value?.let { task ->
            coroutineScope.launch {
                _loadingStatus.value = LoadingStatus.LOADING
                repository.removeMultiProjectTask(projectInput, task)
                _taskSelect.value = null
                _loadingStatus.value = LoadingStatus.DONE
            }
        }
    }

    fun updateProjectCompleteRate(completeRate: Int) {
        project.value?.let { project ->
            coroutineScope.launch {
                _loadingStatus.value = LoadingStatus.LOADING
                repository.updateMultiProjectCompleteRate(project, completeRate)
                _loadingStatus.value = LoadingStatus.DONE
            }
        }
    }

    fun copyTaskToFirebase() {
        _taskSelect.value?.let {
            val newTask = it.newRefTask()
            newTask.firebaseId = ""
            coroutineScope.launch {
                _loadingStatus.value = LoadingStatus.LOADING
                repository.updateMultiProjectTask(projectInput, newTask)
                _loadingStatus.value = LoadingStatus.DONE
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}