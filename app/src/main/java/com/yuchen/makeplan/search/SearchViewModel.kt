package com.yuchen.makeplan.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel (private val repository: MakePlanRepository) : ViewModel(){
    val projects: LiveData<List<MultiProject>> = repository.getAllMultiProjectsFromFirebase()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun sendJoinRequest(project: MultiProject){
        coroutineScope.launch {
            repository.sendJoinRequestToFirebase(project)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}