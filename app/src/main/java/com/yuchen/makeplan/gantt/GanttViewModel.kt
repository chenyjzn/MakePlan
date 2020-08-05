package com.yuchen.makeplan.gantt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.ext.removeFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GanttViewModel(private val repository: MakePlanRepository, private val projectHistory: Array<Project>) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _projectSaveSuccess = MutableLiveData<Boolean>()
    val projectSaveSuccess: LiveData<Boolean>
        get() = _projectSaveSuccess

    var projectRep: MutableList<Project> = projectHistory.toMutableList()

    private val _project = MutableLiveData<Project>().apply {
        value = projectRep.last()
    }
    val project: LiveData<Project>
        get() = _project

    var projectPos = projectRep.lastIndex

    private val _navigateToTaskSetting = MutableLiveData<Int>()
    val navigateToTaskSetting: LiveData<Int>
        get() = _navigateToTaskSetting

    private val _taskSelect = MutableLiveData<Int>().apply {
        value = -1
    }
    val taskSelect: LiveData<Int>
        get() = _taskSelect

    private val _taskTimeScale = MutableLiveData<Int>().apply {
        value = 0
    }
    val taskTimeScale: LiveData<Int>
        get() = _taskTimeScale

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun setNewTaskCondition(taskPos: Int, task: Task) {
        val newProject = projectRep[projectPos].newRefProject()
        newProject.taskList[taskPos].startTimeMillis = task.startTimeMillis
        newProject.taskList[taskPos].endTimeMillis = task.endTimeMillis
        projectRep.removeFrom(projectPos)
        projectRep.add(newProject)
        projectPos = projectRep.lastIndex
        _project.value = projectRep.last()
    }

    fun setTasksSwap(taskList: MutableList<Task>) {
        val newProject = projectRep[projectPos].newRefProject()
        newProject.taskList = taskList
        projectRep.removeFrom(projectPos)
        projectRep.add(newProject)
        projectPos = projectRep.lastIndex
        _project.value = projectRep.last()
    }

    fun setProjectTime(start: Long, end: Long) {
        for (i in projectRep) {
            i.startTimeMillis = start
            i.endTimeMillis = end
        }
        _project.value = projectRep[projectPos]
    }

    fun isTaskSelect(): Int {
        return _taskSelect.value ?: -1
    }

    fun setTaskSelect(taskPos: Int) {
        if (_taskSelect.value != -1 && _taskSelect.value == taskPos)
            _taskSelect.value = -1
        else
            _taskSelect.value = taskPos
    }

    fun goToAddTask() {
        _navigateToTaskSetting.value = -1
    }

    fun goToEditTask(pos: Int) {
        _navigateToTaskSetting.value = pos
    }

    fun goToTaskDone() {
        _navigateToTaskSetting.value = null
    }

    fun unDoAction() {
        if (projectPos != 0) {
            projectPos -= 1
            _project.value = projectRep[projectPos]
        }
    }

    fun reDoAction() {
        if (projectPos != projectRep.lastIndex) {
            projectPos += 1
            _project.value = projectRep[projectPos]
        }
    }

    fun saveProject() {
        _project.value?.let {
            coroutineScope.launch {
                _loadingStatus.value = LoadingStatus.LOADING
                it.updateTime = System.currentTimeMillis()
                repository.updateProject(it)
                _projectSaveSuccess.value = true
                _loadingStatus.value = LoadingStatus.DONE
            }
        }
    }

    fun saveProjectDone() {
        _projectSaveSuccess.value = null
    }

    fun taskRemove(pos: Int) {
        val newProject = projectRep[projectPos].newRefProject()
        newProject.taskList.removeAt(pos)
        projectRep.removeFrom(projectPos)
        projectRep.add(newProject)
        projectPos = projectRep.lastIndex
        _project.value = projectRep.last()
        _taskSelect.value?.let {
            if (projectRep.last().taskList.size == it)
                _taskSelect.value = -1
        }
    }

    fun getUndoListArray(): Array<Project> {
        return projectRep.subList(0, projectPos + 1).toTypedArray()
    }

    fun setTaskTimeScale(time: Int) {
        _taskTimeScale.value = time
    }

    fun copyTask() {
        taskSelect.value?.let {
            val newProject = projectRep[projectPos].newRefProject()
            val newTask = newProject.taskList[it].newRefTask()
            newProject.taskList.add(it, newTask)
            projectRep.removeFrom(projectPos)
            projectRep.add(newProject)
            projectPos = projectRep.lastIndex
            _project.value = projectRep.last()
        }
    }

    fun swapTaskUp() {
        taskSelect.value?.let { pos ->
            val newProject = projectRep[projectPos].newRefProject()
            newProject.taskList[pos] = newProject.taskList[pos - 1].also {
                newProject.taskList[pos - 1] = newProject.taskList[pos]
            }
            projectRep.removeFrom(projectPos)
            projectRep.add(newProject)
            projectPos = projectRep.lastIndex
            _project.value = projectRep.last()
            _taskSelect.value = pos - 1
        }
    }

    fun swapTaskDown() {
        taskSelect.value?.let { pos ->
            val newProject = projectRep[projectPos].newRefProject()
            newProject.taskList[pos] = newProject.taskList[pos + 1].also {
                newProject.taskList[pos + 1] = newProject.taskList[pos]
            }
            projectRep.removeFrom(projectPos)
            projectRep.add(newProject)
            projectPos = projectRep.lastIndex
            _project.value = projectRep.last()
            _taskSelect.value = pos + 1
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}