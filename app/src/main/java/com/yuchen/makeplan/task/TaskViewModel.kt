package com.yuchen.makeplan.task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.HOUR_MILLIS
import com.yuchen.makeplan.MINUTE_MILLIS
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskViewModel (private val repository: MakePlanRepository, private val projectHistory : Array<Project>, private val taskPos : Int, val colorList : List<String>) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var projectRep : MutableList<Project> = projectHistory.toMutableList()
    private val calendarStart = Calendar.getInstance()
    private val calendarEnd = Calendar.getInstance()

    val newStartTimeMillis = MutableLiveData<Long>().apply {
        if (taskPos == -1)
            calendarStart.set(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH), 0, 0)
        else
            calendarStart.timeInMillis = projectHistory.last().taskList[taskPos].startTimeMillis
        value = calendarStart.timeInMillis
    }

    val newEndTimeMillis = MutableLiveData<Long>().apply {
        if (taskPos == -1)
            calendarEnd.timeInMillis = calendarStart.timeInMillis + 1 * DAY_MILLIS
        else
            calendarEnd.timeInMillis = projectHistory.last().taskList[taskPos].endTimeMillis
        value = calendarEnd.timeInMillis
    }

    val newTaskName = MutableLiveData<String>().apply {
        value = if (taskPos == -1)
            "New Task"
        else
            projectHistory.last().taskList[taskPos].name
    }

    val newTaskColor = MutableLiveData<String>().apply {
        value = if (taskPos == -1)
            colorList.first()
        else
            projectHistory.last().taskList[taskPos].color
    }

    val newTaskCompleteRate = MutableLiveData<Int>().apply {
        value = if (taskPos == -1)
            0
        else
            projectHistory.last().taskList[taskPos].completeRate
    }

    private val _newProject = MutableLiveData<MutableList<Project>>()
    val newProject: LiveData<MutableList<Project>>
        get() = _newProject

    fun addTaskToNewProject(){
            val newProject = projectRep.last().newRefProject()
            val name = newTaskName.value ?: "Project"
            val startTimeMillis = newStartTimeMillis.value ?: calendarStart.timeInMillis
            val endTimeMillis = newEndTimeMillis.value ?: calendarEnd.timeInMillis
            val color = newTaskColor.value ?: colorList.first()
            var completeRate = newTaskCompleteRate.value ?: 0
            if (taskPos == -1) {
                newProject.taskList.add(
                    Task(
                        startTimeMillis = startTimeMillis,
                        endTimeMillis = endTimeMillis,
                        name = name,
                        color = color,
                        completeRate = completeRate
                    )
                )
            } else {
                newProject.taskList[taskPos].startTimeMillis = startTimeMillis
                newProject.taskList[taskPos].endTimeMillis = endTimeMillis
                newProject.taskList[taskPos].name = name
                newProject.taskList[taskPos].color = color
                newProject.taskList[taskPos].completeRate = completeRate
            }
            projectRep.add(newProject)
            _newProject.value = projectRep
    }

    fun setStartDate(year: Int, month: Int, dayOfMonth: Int){
        calendarStart.set(year,month,dayOfMonth)
        newStartTimeMillis.value = calendarStart.timeInMillis

        if (calendarStart.timeInMillis > calendarEnd.timeInMillis)
            setEndTimeByTimeMillis(calendarStart.timeInMillis)
    }

    fun setStartTime(hourOfDay: Int, minute: Int){
        calendarStart.set(calendarStart.get(Calendar.YEAR),calendarStart.get(Calendar.MONTH),calendarStart.get(Calendar.DAY_OF_MONTH),hourOfDay,minute)
        newStartTimeMillis.value = calendarStart.timeInMillis

        if (calendarStart.timeInMillis > calendarEnd.timeInMillis)
            setEndTimeByTimeMillis(calendarStart.timeInMillis)
    }

    fun setEndDate(year: Int, month: Int, dayOfMonth: Int){
        calendarEnd.set(year,month,dayOfMonth)
        if (calendarEnd.timeInMillis < calendarStart.timeInMillis)
            calendarEnd.timeInMillis = calendarStart.timeInMillis

        newEndTimeMillis.value = calendarEnd.timeInMillis
    }

    fun setEndTime(hourOfDay: Int, minute: Int){
        calendarEnd.set(calendarEnd.get(Calendar.YEAR),calendarEnd.get(Calendar.MONTH),calendarEnd.get(Calendar.DAY_OF_MONTH),hourOfDay,minute)
        if (calendarEnd.timeInMillis < calendarStart.timeInMillis)
            calendarEnd.timeInMillis = calendarStart.timeInMillis

        newEndTimeMillis.value = calendarEnd.timeInMillis
    }

    fun setEndTimeByTimeMillis(timeMillis : Long){
        newEndTimeMillis.value = timeMillis
        calendarEnd.timeInMillis = timeMillis
    }

    fun convertTimeMilliToDateString(value: Long?): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        if (value == null)
            return value.toString()
        else
            return simpleDateFormat.format(value)
    }

    fun convertTimeMilliToTimeString(value: Long?): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        if (value == null)
            return value.toString()
        else
            return simpleDateFormat.format(value)
    }

    fun setEndByDurationDay(day : Int){
        val oldDuration = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val newDuration = oldDuration - (oldDuration / DAY_MILLIS) * DAY_MILLIS + day * DAY_MILLIS
        newStartTimeMillis.value?.let {
            newEndTimeMillis.value = it + newDuration
            calendarEnd.timeInMillis = it + newDuration
        }
    }

    fun setEndByDurationHour(hour : Int){
        val oldDuration = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val newDuration = oldDuration - (((oldDuration) % DAY_MILLIS)/HOUR_MILLIS) * HOUR_MILLIS + hour * HOUR_MILLIS
        newStartTimeMillis.value?.let {
            newEndTimeMillis.value = it + newDuration
            calendarEnd.timeInMillis = it + newDuration
        }
    }

    fun setEndByDurationMinute(minute : Int){
        val oldDuration = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val newDuration = oldDuration - ((((oldDuration) % DAY_MILLIS)% HOUR_MILLIS)/ MINUTE_MILLIS) * MINUTE_MILLIS + minute * MINUTE_MILLIS
        newStartTimeMillis.value?.let {
            newEndTimeMillis.value = it + newDuration
            calendarEnd.timeInMillis = it + newDuration
        }
    }

    fun getDurationDays(start : Long , end : Long) : String{
        return ((end - start) / DAY_MILLIS).toString()
    }

    fun getDurationHours(start : Long , end : Long) : String{
        return (((end - start) % DAY_MILLIS)/ HOUR_MILLIS).toString()
    }

    fun getDurationMinutes(start : Long , end : Long) : String{
        return ((((end - start) % DAY_MILLIS)% HOUR_MILLIS)/ MINUTE_MILLIS).toString()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}