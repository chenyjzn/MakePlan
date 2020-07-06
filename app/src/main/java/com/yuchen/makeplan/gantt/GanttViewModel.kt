package com.yuchen.makeplan.gantt

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.TimeUtil.StampToDate
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import java.util.*

class GanttViewModel (application: Application) : AndroidViewModel(application) {

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project>
        get() = _project

    private val projectRep : MutableList<Project> = mutableListOf()

    init {
        val c = Calendar.getInstance()
        var s = c.timeInMillis
        c.add(Calendar.DAY_OF_YEAR,20)
        var e = c.timeInMillis
        var project = Project(s,e,"Project", listOf())
        c.set(2020,6,4,12,0,0)
        s=c.timeInMillis
        c.set(2020,6,9,12,0,0)
        e=c.timeInMillis
        var task1 = Task(s,e,"Task1")
        c.set(2020,6,8,6,0,0)
        s=c.timeInMillis
        c.set(2020,6,30,18,0,0)
        e=c.timeInMillis
        var task2 = Task(s,e,"Task2")
        c.set(2020,6,8,6,0,0)
        s=c.timeInMillis
        c.set(2020,6,30,18,0,0)
        e=c.timeInMillis
        var task3 = Task(s,e,"Task3")
        c.set(2020,6,1,10,0,0)
        s=c.timeInMillis
        c.set(2020,6,30,18,0,0)
        e=c.timeInMillis
        var task4 = Task(s,e,"Task4")
        c.set(2020,6,6,0,0,0)
        s=c.timeInMillis
        c.set(2020,6,20,0,0,0)
        e=c.timeInMillis
        var task5 = Task(s,e,"Task5")
        c.set(2020,7,6,0,0,0)
        s=c.timeInMillis
        c.set(2020,7,20,0,0,0)
        e=c.timeInMillis
        var task6 = Task(s,e,"Task6")
        c.set(2020,7,6,0,0,0)
        s=c.timeInMillis
        c.set(2020,7,20,0,0,0)
        e=c.timeInMillis
        var task7 = Task(s,e,"Task7")
        project.taskList = listOf(task1,task2,task3,task4,task5,task6,task7)
        //Log.d("chenyjzn","${StampToDate(project.startTimeMillis)} , ${StampToDate(project.endTimeMillis)} , ${StampToDate(project.taskList[0].startTimeMillis)} , ${StampToDate(project.taskList[0].endTimeMillis)}")
        projectRep.add(project)
        _project.value = projectRep.last()
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