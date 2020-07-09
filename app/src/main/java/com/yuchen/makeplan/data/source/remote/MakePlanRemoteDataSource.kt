package com.yuchen.makeplan.data.source.remote

import androidx.lifecycle.LiveData
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanDataSource

object MakePlanRemoteDataSource :MakePlanDataSource {
    override suspend fun insertProject(project: Project) {
        TODO("Not yet implemented")
    }

    override suspend fun updateProject(project: Project) {
        TODO("Not yet implemented")
    }

    override fun getAllProjects(): LiveData<List<Project>> {
        TODO("Not yet implemented")
    }
}