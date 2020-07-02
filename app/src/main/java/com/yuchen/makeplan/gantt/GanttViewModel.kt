package com.yuchen.makeplan.gantt

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yuchen.makeplan.TimeUtil.StampToDate
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import java.util.*

class GanttViewModel (application: Application) : AndroidViewModel(application) {

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project>
        get() = _project

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
        project.taskList = listOf(task1,task2)
        //Log.d("chenyjzn","${StampToDate(project.startTimeMillis)} , ${StampToDate(project.endTimeMillis)} , ${StampToDate(project.taskList[0].startTimeMillis)} , ${StampToDate(project.taskList[0].endTimeMillis)}")

        _project.value = project
    }
}