package com.yuchen.makeplan.joinuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class JoinUserViewModel(private val repository: MakePlanRepository, private val project: Project) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val users: LiveData<List<User>> = repository.getMultiProjectJoinRequsetFromFirebase(project)

    fun confirmUserJoin(user: User){

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}