package com.yuchen.makeplan.task

import android.util.Log
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.HOUR_MILLIS
import com.yuchen.makeplan.MINUTE_MILLIS
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.ToDo
import com.yuchen.makeplan.data.source.MakePlanRepository
import java.text.SimpleDateFormat
import java.util.*

class TaskViewModel (private val repository: MakePlanRepository, private val projectHistory : Array<Project>, private val pos : Int, val colorList : List<String>) : ViewModel() {

    var projectRep : MutableList<Project> = projectHistory.toMutableList()
    val calendarStart = Calendar.getInstance()
    val calendarEnd = Calendar.getInstance()

    val newStartTimeMillis = MutableLiveData<Long>().apply {
        if (pos == -1)
            calendarStart.set(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH), 0, 0)
        else
            calendarStart.timeInMillis = projectHistory.last().taskList[pos].startTimeMillis
        value = calendarStart.timeInMillis
    }

    val newEndTimeMillis = MutableLiveData<Long>().apply {
        if (pos == -1)
            calendarEnd.timeInMillis = calendarStart.timeInMillis + 1 * DAY_MILLIS
        else
            calendarEnd.timeInMillis = projectHistory.last().taskList[pos].endTimeMillis
        value = calendarEnd.timeInMillis
    }

    var duration = 0L

    val newTaskName = MutableLiveData<String>().apply {
        value = if (pos == -1)
            "New Task"
        else
            projectHistory.last().taskList[pos].name
    }
    val newTaskColor = MutableLiveData<String>().apply {
        value = if (pos == -1)
            colorList.first()
        else
            projectHistory.last().taskList[pos].color
    }

    private val _taskPos = MutableLiveData<Int>().apply {
        value = pos
    }
    val taskPos: LiveData<Int>
        get() = _taskPos

    val newTask = MutableLiveData<Task>().apply {
        if (pos == -1)
            value = Task()
        else
            value = projectHistory.last().taskList[pos].newRefTask()
    }

    private val _newProject = MutableLiveData<MutableList<Project>>()
    val newProject: LiveData<MutableList<Project>>
        get() = _newProject

    fun addTaskToNewProject(){
        val newProject =projectRep.last().newRefProject()
        newTask.value?.let { newProject.taskList.add(it) }
        projectRep.add(newProject)
        _newProject.value = projectRep
    }

    fun setStartDate(year: Int, month: Int, dayOfMonth: Int){
        calendarStart.set(year,month,dayOfMonth)
        newStartTimeMillis.value = calendarStart.timeInMillis
    }

    fun setStartTime(hourOfDay: Int, minute: Int){
        calendarStart.set(calendarStart.get(Calendar.YEAR),calendarStart.get(Calendar.MONTH),calendarStart.get(Calendar.DAY_OF_MONTH),hourOfDay,minute)
        newStartTimeMillis.value = calendarStart.timeInMillis
    }

    fun setEndtDate(year: Int, month: Int, dayOfMonth: Int){
        calendarEnd.set(year,month,dayOfMonth)
        newEndTimeMillis.value = calendarEnd.timeInMillis
    }

    fun setEndTime(hourOfDay: Int, minute: Int){
        calendarEnd.set(calendarEnd.get(Calendar.YEAR),calendarEnd.get(Calendar.MONTH),calendarEnd.get(Calendar.DAY_OF_MONTH),hourOfDay,minute)
        newEndTimeMillis.value = calendarEnd.timeInMillis
    }

    fun setEndTimeByTimeMillis(timeMillis : Long){
        newEndTimeMillis.value = timeMillis
        calendarEnd.timeInMillis = timeMillis
    }

    @InverseMethod("convertTimeMilliToDateString")
    fun convertDateStringToTimeMilli(value: String): Long {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        return simpleDateFormat.parse(value).time
    }
    fun convertTimeMilliToDateString(value: Long?): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        if (value == null)
            return value.toString()
        else
            return simpleDateFormat.format(value)
    }

    @InverseMethod("convertTimeMilliToTimeString")
    fun convertTimeStringToTimeMilli(value: String): Long {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        return simpleDateFormat.parse(value).time
    }
    fun convertTimeMilliToTimeString(value: Long?): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        if (value == null)
            return value.toString()
        else
            return simpleDateFormat.format(value)
    }

    fun getDurationDays(start : Long, end : Long) : String{
        return ((end - start) / DAY_MILLIS).toString()
    }

    fun getDurationHours(start : Long, end : Long) : String{
        return (((end - start) % DAY_MILLIS)/ HOUR_MILLIS).toString()
    }

    fun getDurationMinutes(start : Long, end : Long) : String{
        return ((((end - start) % DAY_MILLIS)% HOUR_MILLIS)/ MINUTE_MILLIS).toString()
    }
}