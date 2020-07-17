package com.yuchen.makeplan.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Team
import com.yuchen.makeplan.data.User
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

    override suspend fun searchProject(id: Long): Project? {
        return withContext(Dispatchers.IO) {
            MakePlanDataBase.getInstance(context).makePlanDataBaseDao.searchProject(id)
        }
    }

    override fun getAllProjects(): LiveData<List<Project>> {
        return MakePlanDataBase.getInstance(context).makePlanDataBaseDao.getAllProjects()
    }

    override suspend fun removeProjectFromFirebase(id: Long): Result<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>) : Result<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadPersonalProjectsFromFirebase(): Result<List<Project>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserInfoToFirebase(): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun addTeamToFirebase(teamName : String): Result<String> {
        TODO("Not yet implemented")
    }

    override fun getUserTeamsFromFirebase(): MutableLiveData<List<Team>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTeamByTextFromFirebase(text: String): Result<List<Team>> {
        TODO("Not yet implemented")
    }

    override fun getAllTeamsFromFirebase(): MutableLiveData<List<Team>> {
        TODO("Not yet implemented")
    }

    override suspend fun createTeamToFirebase(teamName: String): Result<String> {
        TODO("Not yet implemented")
    }

    override fun getMyMultiProjectsFromFirebase(): LiveData<List<Project>> {
        TODO("Not yet implemented")
    }

    override suspend fun addMultiProjectToFirebase(project: Project): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMultiProjectToFirebase(project: Project): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun removeMultiProjectFromFirebase(id: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllMultiProjectsFromFirebase(): LiveData<List<Project>> {
        TODO("Not yet implemented")
    }
}
