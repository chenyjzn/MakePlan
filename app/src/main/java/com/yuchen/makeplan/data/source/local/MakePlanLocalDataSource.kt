package com.yuchen.makeplan.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MakePlanLocalDataSource(val context: Context) : MakePlanDataSource {
    override suspend fun insertProject(project: Project) {
        withContext(Dispatchers.IO) {
            MakePlanDataBase.getInstance(context).makePlanDataBaseDao.insertProject(project)
        }
    }

    override suspend fun updateProject(project: Project) {
        withContext(Dispatchers.IO) {
            MakePlanDataBase.getInstance(context).makePlanDataBaseDao.updateProject(project)
        }
    }

    override suspend fun removeProject(project: Project) {
        withContext(Dispatchers.IO) {
            MakePlanDataBase.getInstance(context).makePlanDataBaseDao.removeProject(project)
        }
    }

    override fun getAllProjects(): LiveData<List<Project>> {
        return MakePlanDataBase.getInstance(context).makePlanDataBaseDao.getAllProjects()
    }
}
