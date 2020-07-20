package com.yuchen.makeplan.multitask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.HOUR_MILLIS
import com.yuchen.makeplan.MINUTE_MILLIS
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MultiTaskViewModel (private val repository: MakePlanRepository, private val projectInput: Project,private val taskInput: Task?, application: Application) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val calendarStart = Calendar.getInstance()
    private val calendarEnd = Calendar.getInstance()

    val colorList = application.resources.getStringArray(R.array.color_array).toList()

    val project: LiveData<Project> = repository.getMultiProjectFromFirebase(projectInput)

    val newStartTimeMillis = MutableLiveData<Long>().apply {
        if (taskInput == null)
            calendarStart.set(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH), 0, 0)
        else
            calendarStart.timeInMillis = taskInput.startTimeMillis
        value = calendarStart.timeInMillis
    }

    val newEndTimeMillis = MutableLiveData<Long>().apply {
        if (taskInput == null)
            calendarEnd.timeInMillis = calendarStart.timeInMillis + 1 * DAY_MILLIS
        else
            calendarEnd.timeInMillis = taskInput.endTimeMillis
        value = calendarEnd.timeInMillis
    }

    val newTaskName = MutableLiveData<String>().apply {
        value = if (taskInput == null)
            "New Task"
        else
            taskInput.name
    }

    val newTaskColor = MutableLiveData<String>().apply {
        value = if (taskInput == null)
            colorList.first()
        else
            taskInput.color
    }

    val newTaskCompleteRate = MutableLiveData<Int>().apply {
        value = if (taskInput == null)
            0
        else
            taskInput.completeRate
    }

    private val _saveTask = MutableLiveData<Boolean>()
    val saveTask: LiveData<Boolean>
        get() = _saveTask

    fun updateTaskToFirebase(){
        val name = newTaskName.value ?: "Project"
        val startTimeMillis = newStartTimeMillis.value ?: calendarStart.timeInMillis
        val endTimeMillis = newEndTimeMillis.value ?: calendarEnd.timeInMillis
        val color = newTaskColor.value ?: colorList.first()
        var completeRate = newTaskCompleteRate.value ?: 0
        var newTask = Task(startTimeMillis = startTimeMillis, endTimeMillis = endTimeMillis, name = name, color = color, completeRate = completeRate)
        if (taskInput != null) {
            newTask.firebaseId = taskInput.firebaseId
        }
        coroutineScope.launch {
            repository.updateMultiProjectTaskToFirebase(projectInput,newTask)
            _saveTask.value = true
        }
    }

    fun saveTaskDone(){
        _saveTask.value = null
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