package com.yuchen.makeplan.gantt

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

class GanttViewModel (private val repository: MakePlanRepository , private val projectHistory : Array<Project>,val isMultiProject:Boolean) : ViewModel() {

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

    val multiProject: LiveData<Project>? = if (isMultiProject){
        repository.getMultiProjectFromFirebase(projectRep.last())
    }else{
        null
    }

    var projectPos = projectRep.lastIndex

    private val _navigateToTaskSetting = MutableLiveData<Int>()
    val navigateToTaskSetting: LiveData<Int>
        get() = _navigateToTaskSetting

    private val _taskSelect = MutableLiveData<Int>().apply {
        value = -1
    }
    val taskSelect: LiveData<Int>
        get() = _taskSelect

    fun pushProjectRep(project: Project){
        projectRep.add(project)
        projectPos += 1
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

    fun addProjectToRep(){
        //Log.d("chenyjzn","add project = ${_project.value}")
        _project.value?.let {
            projectRep.removeFrom(projectPos)
            projectRep.add(it.newRefProject())
        }
        projectPos = projectRep.lastIndex
        _project.value = projectRep.last()
    }

    fun setProjectTimeByDx(dx : Float, width : Int){
        if (isMultiProject){
            Log.d("chenyjzn","set ${project.value}")
            var timeOffset = ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat()*dx/width.toFloat()).toLong()
            projectRep.forEach {
                it.startTimeMillis -= timeOffset
                it.endTimeMillis -= timeOffset
            }
            coroutineScope.launch {
                repository.updateMultiProjectToFirebase(projectRep[projectPos])
            }
        } else{
            var timeOffset = ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat()*dx/width.toFloat()).toLong()
            projectRep.forEach {
                it.startTimeMillis -= timeOffset
                it.endTimeMillis -= timeOffset
            }
            _project.value = projectRep[projectPos]
        }
    }

    fun setProjectTimeByDlDr(dl : Float, dr : Float, width : Int){
        if (isMultiProject){
            var timeOffsetl =
                ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat() * dl / width.toFloat()).toLong()
            var timeOffsetr =
                ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat() * dr / width.toFloat()).toLong()
            projectRep.forEach {
                it.startTimeMillis += timeOffsetl
                it.endTimeMillis -= timeOffsetr
            }
            coroutineScope.launch {
                repository.updateMultiProjectToFirebase(projectRep[projectPos])
            }
        } else {
            var timeOffsetl =
                ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat() * dl / width.toFloat()).toLong()
            var timeOffsetr =
                ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat() * dr / width.toFloat()).toLong()
            projectRep.forEach {
                it.startTimeMillis += timeOffsetl
                it.endTimeMillis -= timeOffsetr
            }
            _project.value = projectRep[projectPos]
        }
    }

    fun getUndoListArray(): Array<Project>{
        return projectRep.subList(0,projectPos+1).toTypedArray()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}