package com.yuchen.makeplan.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.*
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

    override suspend fun removePersonalProjectFromFirebase(id: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadPersonalProjectsFromFirebase(): Result<List<Project>> {
        TODO("Not yet implemented")
    }

    override suspend fun checkUserExistInFirebase(): Result<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserInfoToUsers(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserInfoToMultiProjects(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser?> {
        TODO("Not yet implemented")
    }

    override fun getAllMultiProjects(): LiveData<List<MultiProject>> {
        TODO("Not yet implemented")
    }

    override fun getMultiProject(project: MultiProject): LiveData<MultiProject> {
        TODO("Not yet implemented")
    }

    override fun getMultiProjectTasks(project: MultiProject): LiveData<List<MultiTask>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMultiProjectTask(project: MultiProject, task: MultiTask): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeMultiProjectTask(project: MultiProject, task: MultiTask): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMultiProjectCompleteRate(project: MultiProject, completeRate: Int): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): LiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun addMultiProject(project: MultiProject): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMultiProject(project: MultiProject): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeMultiProject(project: MultiProject): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMyMultiProjects(field: String): LiveData<List<MultiProject>> {
        TODO("Not yet implemented")
    }

    override suspend fun requestUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun approveUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeUserToMultiProject(project: MultiProject, user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMultiProjectUsersUid(project: MultiProject, field: String): LiveData<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersByUidList(uidList: List<String>): Result<List<User>> {
        TODO("Not yet implemented")
    }
}
