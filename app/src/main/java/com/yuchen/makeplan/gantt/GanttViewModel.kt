package com.yuchen.makeplan.gantt

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
import java.util.*

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

    fun goToAddTask(){
        _navigateToTaskSetting.value = -1
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
                repository.updateProject(it)
                _projectSaveSuccess.value = true
            }
        }
    }

    fun saveProjectDone(){
        _projectSaveSuccess.value = null
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
//        val newProject = _project.value?.newRefProject()?: projectRep[projectPos]
        var timeOffset = ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat()*dx/width.toFloat()).toLong()
//        newProject.startTimeMillis -= timeOffset
//        newProject.endTimeMillis -= timeOffset
        projectRep.forEach {
            it.startTimeMillis -= timeOffset
            it.endTimeMillis -= timeOffset
        }
        _project.value = projectRep[projectPos]
    }

    fun setProjectTimeBy2Dx(dx1 : Float, dx2 : Float, width : Int){
//        val newProject = _project.value?.newRefProject()?: projectRep[projectPos]
        var timeOffset1 = ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat()*dx1/width.toFloat()).toLong()
        var timeOffset2 = ((projectRep[projectPos].endTimeMillis - projectRep[projectPos].startTimeMillis).toFloat()*dx2/width.toFloat()).toLong()
//        newProject.startTimeMillis -= timeOffset1
//        newProject.endTimeMillis -= timeOffset2
        projectRep.forEach {
            it.startTimeMillis -= timeOffset1
            it.endTimeMillis -= timeOffset2
        }
        _project.value = projectRep[projectPos]
    }

    fun getUndoListArray(): Array<Project>{
        return projectRep.subList(0,projectPos+1).toTypedArray()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}