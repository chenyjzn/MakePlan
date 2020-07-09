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
import java.util.*

class GanttViewModel (private val repository: MakePlanRepository , private val projectHistory : Array<Project>,private val projectHistoryPos : Int) : ViewModel() {

    var projectRep : MutableList<Project> = projectHistory.toMutableList()
    private var projectRepPos : Int = projectHistoryPos

    private val _project = MutableLiveData<Project>().apply {
        value = projectRep[projectRepPos]
    }
    val project: LiveData<Project>
        get() = _project

    private val _navigateToTaskSetting = MutableLiveData<Int>()
    val navigateToTaskSetting: LiveData<Int>
        get() = _navigateToTaskSetting

    fun goToAddTask(){
        _navigateToTaskSetting.value = -1
    }

    fun goToTaskDone(){
        _navigateToTaskSetting.value = null
    }

    fun setProjectTimeByDx(dx : Float, width : Int){
        val newProject = projectRep.last()
        var timeOffset = ((newProject.endTimeMillis - newProject.startTimeMillis).toFloat()*dx/width.toFloat()).toLong()
        newProject.startTimeMillis -= timeOffset
        newProject.endTimeMillis -= timeOffset
        _project.value = projectRep.last()
    }

    fun setProjectTimeBy2Dx(dx1 : Float, dx2 : Float, width : Int){
        val newProject = projectRep.last()
        var timeOffset1 = ((newProject.endTimeMillis - newProject.startTimeMillis).toFloat()*dx1/width.toFloat()).toLong()
        var timeOffset2 = ((newProject.endTimeMillis - newProject.startTimeMillis).toFloat()*dx2/width.toFloat()).toLong()
        newProject.startTimeMillis -= timeOffset1
        newProject.endTimeMillis -= timeOffset2

        _project.value = projectRep.last()
    }

}