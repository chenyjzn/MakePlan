package com.yuchen.makeplan.notexist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository

class NotExistCheckViewModel (private val repository: MakePlanRepository, private val projects : Array<Project>): ViewModel(){

    private val _notExistProjects = MutableLiveData<List<Project>>().apply {
        value = projects.toList()
    }
    val notExistProjects: LiveData<List<Project>>
        get() = _notExistProjects
}