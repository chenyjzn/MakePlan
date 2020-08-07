package com.yuchen.makeplan.multiProjects

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_MEMBERS_UID

class MultiProjectsViewModel(private val repository: MakePlanRepository) : ViewModel() {
    val projects: LiveData<List<MultiProject>> = repository.getMyMultiProjects(FIELD_MEMBERS_UID)
}