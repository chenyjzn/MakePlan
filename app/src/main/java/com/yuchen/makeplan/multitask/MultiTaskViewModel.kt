package com.yuchen.makeplan.multitask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yuchen.makeplan.*
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.MultiTask
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.util.TimeUtil.millisToHourMinutes
import com.yuchen.makeplan.util.TimeUtil.millisToYearMonth2Day
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MultiTaskViewModel(
    private val repository: MakePlanRepository,
    private val projectInput: MultiProject,
    private val taskInput: MultiTask?,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val calendarStart = Calendar.getInstance()
    private val calendarEnd = Calendar.getInstance()

    val colorList = application.resources.getStringArray(R.array.color_array_1).toList()

    val project: LiveData<MultiProject> = repository.getMultiProject(projectInput)

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

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

    private val _newDuration = MutableLiveData<Long>()
    val newDuration: LiveData<Long>
        get() = _newDuration

    val newTaskName = MutableLiveData<String>().apply {
        value = taskInput?.name ?: "New Task"
    }

    val newTaskColorPair = MutableLiveData<Pair<Int, String>>().apply {
        value = if (taskInput == null)
            0 to colorList[0]
        else
            taskInput.color to colorList[taskInput.color]
    }

    val newTaskCompleteRate = MutableLiveData<Int>().apply {
        value = taskInput?.completeRate ?: 0
    }

    private val _saveTask = MutableLiveData<Boolean>()
    val saveTask: LiveData<Boolean>
        get() = _saveTask

    fun setNewDuration() {
        newStartTimeMillis.value?.let { start ->
            newEndTimeMillis.value?.let { end ->
                _newDuration.value = end - start
            }
        }
    }

    fun updateTaskToFirebase() {
        val name = newTaskName.value ?: "Project"
        val startTimeMillis = newStartTimeMillis.value ?: calendarStart.timeInMillis
        val endTimeMillis = newEndTimeMillis.value ?: calendarEnd.timeInMillis
        val color = newTaskColorPair.value?.first ?: 0
        val completeRate = newTaskCompleteRate.value ?: 0
        val newTask = MultiTask(
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            name = name,
            color = color,
            completeRate = completeRate
        )
        if (taskInput != null) {
            newTask.firebaseId = taskInput.firebaseId
        }
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.updateMultiProjectTask(projectInput, newTask)
            when (result) {
                is Result.Success -> {

                }
                is Result.Error -> {
                    _loadingStatus.value = LoadingStatus.ERROR("${result.exception}")
                }
                is Result.Fail -> {
                    _loadingStatus.value = LoadingStatus.ERROR(result.error)
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
            _saveTask.value = true
        }
    }

    fun saveTaskDone() {
        _saveTask.value = null
    }

    fun setStartDate(year: Int, month: Int, dayOfMonth: Int) {
        calendarStart.set(year, month, dayOfMonth)
        newStartTimeMillis.value = calendarStart.timeInMillis

        if (calendarStart.timeInMillis > calendarEnd.timeInMillis)
            setEndTimeByTimeMillis(calendarStart.timeInMillis)
    }

    fun setStartTime(hourOfDay: Int, minute: Int) {
        calendarStart.set(
            calendarStart.get(Calendar.YEAR),
            calendarStart.get(Calendar.MONTH),
            calendarStart.get(Calendar.DAY_OF_MONTH),
            hourOfDay,
            minute
        )
        newStartTimeMillis.value = calendarStart.timeInMillis

        if (calendarStart.timeInMillis > calendarEnd.timeInMillis)
            setEndTimeByTimeMillis(calendarStart.timeInMillis)
    }

    fun setEndDate(year: Int, month: Int, dayOfMonth: Int) {
        calendarEnd.set(year, month, dayOfMonth)
        if (calendarEnd.timeInMillis < calendarStart.timeInMillis)
            calendarEnd.timeInMillis = calendarStart.timeInMillis

        newEndTimeMillis.value = calendarEnd.timeInMillis
    }

    fun setEndTime(hourOfDay: Int, minute: Int) {
        calendarEnd.set(
            calendarEnd.get(Calendar.YEAR),
            calendarEnd.get(Calendar.MONTH),
            calendarEnd.get(Calendar.DAY_OF_MONTH),
            hourOfDay,
            minute
        )
        if (calendarEnd.timeInMillis < calendarStart.timeInMillis)
            calendarEnd.timeInMillis = calendarStart.timeInMillis

        newEndTimeMillis.value = calendarEnd.timeInMillis
    }

    private fun setEndTimeByTimeMillis(timeMillis: Long) {
        newEndTimeMillis.value = timeMillis
        calendarEnd.timeInMillis = timeMillis
    }

    fun convertTimeMilliToDateString(value: Long?): String {
        return if (value == null)
            value.toString()
        else
            millisToYearMonth2Day(value)
    }

    fun convertTimeMilliToTimeString(value: Long?): String {
        return if (value == null)
            value.toString()
        else
            millisToHourMinutes(value)
    }

    fun setEndByDurationDay(day: Int) {
        val oldDuration = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val newDuration = oldDuration - (oldDuration / DAY_MILLIS) * DAY_MILLIS + day * DAY_MILLIS
        newStartTimeMillis.value?.let {
            newEndTimeMillis.value = it + newDuration
            calendarEnd.timeInMillis = it + newDuration
        }
    }

    fun setEndByDurationHour(hour: Int) {
        val oldDuration = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val newDuration = oldDuration - (((oldDuration) % DAY_MILLIS) / HOUR_MILLIS) * HOUR_MILLIS + hour * HOUR_MILLIS
        newStartTimeMillis.value?.let {
            newEndTimeMillis.value = it + newDuration
            calendarEnd.timeInMillis = it + newDuration
        }
    }

    fun setEndByDurationMinute(minute: Int) {
        val oldDuration = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val newDuration = oldDuration - ((((oldDuration) % DAY_MILLIS) % HOUR_MILLIS) / MINUTE_MILLIS) * MINUTE_MILLIS + minute * MINUTE_MILLIS
        newStartTimeMillis.value?.let {
            newEndTimeMillis.value = it + newDuration
            calendarEnd.timeInMillis = it + newDuration
        }
    }

    fun getDurationDays(start: Long, end: Long): String {
        return ((end - start) / DAY_MILLIS).toString()
    }

    fun getDurationHours(start: Long, end: Long): String {
        return (((end - start) % DAY_MILLIS) / HOUR_MILLIS).toString()
    }

    fun getDurationMinutes(start: Long, end: Long): String {
        return ((((end - start) % DAY_MILLIS) % HOUR_MILLIS) / MINUTE_MILLIS).toString()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}