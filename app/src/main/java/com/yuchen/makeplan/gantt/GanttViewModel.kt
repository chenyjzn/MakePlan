package com.yuchen.makeplan.gantt

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

class GanttViewModel (private val repository: MakePlanRepository , private val projectHistory : Array<Project>) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _projectSaveSuccess = MutableLiveData<Boolean>()
    val projectSaveSuccess: LiveData<Boolean>
        get() = _projectSaveSuccess

    var projectRep : MutableList<Project> = projectHistory.toMutableList()

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

    fun setNewTaskCondition(taskPos: Int,task : Task){
        Log.d("chenyjzn","project $projectPos repo : start ${projectRep[projectPos].taskList[0].startTimeMillis}  end ${projectRep[projectPos].taskList[0].endTimeMillis}")
        val newProject = projectRep[projectPos].newRefProject()
        newProject.taskList[taskPos].startTimeMillis = task.startTimeMillis
        newProject.taskList[taskPos].endTimeMillis = task.endTimeMillis
        projectRep.removeFrom(projectPos)
        projectRep.add(newProject)
        projectPos = projectRep.lastIndex
        Log.d("chenyjzn","project ${projectRep.lastIndex} repo : start ${projectRep.last().taskList[0].startTimeMillis}  end ${projectRep.last().taskList[0].endTimeMillis}")
        _project.value = projectRep.last()
    }

    fun setProjectTime(start : Long, end : Long){
        for (i in projectRep){
            i.startTimeMillis = start
            i.endTimeMillis = end
        }
        _project.value = projectRep[projectPos]
    }

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

    fun unDoAction(){
        if (projectPos!=0){
            projectPos-=1
            _project.value = projectRep[projectPos]
        }
    }

    fun reDoAction(){
        if (projectPos!=projectRep.lastIndex){
            projectPos+=1
            _project.value = projectRep[projectPos]
        }
    }

    fun saveProject(){
        _project.value?.let {
            coroutineScope.launch {
                it.updateTime = System.currentTimeMillis()
                repository.updateProject(it)
                _projectSaveSuccess.value = true
            }
        }
    }

    fun saveProjectDone(){
        _projectSaveSuccess.value = null
    }

    fun taskRemove(pos : Int){
        val newProject = projectRep[projectPos].newRefProject()
        newProject.taskList.removeAt(pos)
        projectRep.removeFrom(projectPos)
        projectRep.add(newProject)
        projectPos = projectRep.lastIndex
        _project.value = projectRep.last()
        _taskSelect.value =-1
    }

    fun getUndoListArray(): Array<Project>{
        return projectRep.subList(0,projectPos+1).toTypedArray()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}