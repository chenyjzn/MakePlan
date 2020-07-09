package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import com.yuchen.makeplan.data.Project

interface MakePlanRepository {
    suspend fun insertProject(project: Project)

    suspend fun updateProject(project: Project)

    fun getAllProjects(): LiveData<List<Project>>
}