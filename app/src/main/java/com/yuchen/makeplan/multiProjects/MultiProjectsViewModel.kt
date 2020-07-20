package com.yuchen.makeplan.multiProjects

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MultiProjectsViewModel(private val repository: MakePlanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val projects: LiveData<List<MultiProject>> = repository.getMyMultiProjectsFromFirebase()

    private val _navigateToGantt = MutableLiveData<MultiProject>()
    val navigateToGantt: LiveData<MultiProject>
        get() = _navigateToGantt

    private val _navigateToProjectSetting = MutableLiveData<MultiProject>()
    val navigateToProjectSetting: LiveData<MultiProject>
        get() = _navigateToProjectSetting

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun goToGantt(project: MultiProject) {
        _navigateToGantt.value = project
    }

    fun goToGanttDone() {
        _navigateToGantt.value = null
    }

    fun goToProjectSetting(project: MultiProject?) {
        _navigateToProjectSetting.value = project
    }

    fun goToProjectSettingDone() {
        _navigateToProjectSetting.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}