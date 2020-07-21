package com.yuchen.makeplan.members

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_MEMBERS
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_RECEIVE
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.COLLECTION_SEND
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MembersItemsViewModel(private val repository: MakePlanRepository, private val project: MultiProject, private val membersPos : Int) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val users: LiveData<List<User>> = when(membersPos){
        0 -> repository.getMultiProjectUsers(project,COLLECTION_MEMBERS)
        1 -> repository.getMultiProjectUsers(project,COLLECTION_SEND)
        2 -> repository.getMultiProjectUsers(project,COLLECTION_RECEIVE)
        else -> throw IllegalArgumentException("Unknown pager position!!")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}