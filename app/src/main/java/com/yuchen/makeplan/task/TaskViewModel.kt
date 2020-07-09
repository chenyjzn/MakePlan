package com.yuchen.makeplan.task

import android.util.Log
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.ToDo
import com.yuchen.makeplan.data.source.MakePlanRepository

class TaskViewModel (private val repository: MakePlanRepository, private val projectHistory : Array<Project>, private val pos : Int) : ViewModel() {

    var projectRep : MutableList<Project> = projectHistory.toMutableList()

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

}