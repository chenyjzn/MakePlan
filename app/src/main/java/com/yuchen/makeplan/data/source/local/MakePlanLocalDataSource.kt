package com.yuchen.makeplan.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    override suspend fun removeProjectFromFirebase(id: Long): Result<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>): Result<Int> {
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

    override fun getMyMultiProjectsFromFirebase(): LiveData<List<MultiProject>> {
        TODO("Not yet implemented")
    }

    override suspend fun addMultiProjectToFirebase(project: MultiProject): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMultiProjectToFirebase(project: MultiProject): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun removeMultiProjectFromFirebase(project: MultiProject): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllMultiProjectsFromFirebase(): LiveData<List<MultiProject>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendJoinRequestToFirebase(project: MultiProject): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMultiProjectFromFirebase(project: MultiProject): LiveData<MultiProject> {
        TODO("Not yet implemented")
    }

    override fun getMultiProjectTasksFromFirebase(project: MultiProject): LiveData<List<MultiTask>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMultiProjectTaskToFirebase(
        project: MultiProject,
        task: MultiTask
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeMultiProjectTaskFromFirebase(
        project: MultiProject,
        task: MultiTask
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMultiProjectCompleteRateToFirebase(
        project: MultiProject,
        completeRate: Int
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMultiProjectUsersFromFirebase(project: MultiProject): LiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getUsersFromFirebase(): LiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getMultiProjectJoinRequestFromFirebase(project: MultiProject): LiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMultiProjectUsersToFirebase(
        project: MultiProject,
        users: List<User>
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeMultiProjectUsersFromFirebase(
        project: MultiProject,
        user: User
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun multiProjectInviteUser(
        project: MultiProject,
        user: User
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMultiProjectInviteRequestFromFirebase(project: MultiProject): LiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun multiProjectCancelInviteFromFirebase(
        project: MultiProject,
        user: User
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun multiProjectConfirmUserJoinFirebase(
        project: MultiProject,
        user: User
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMyMultiProjectsFromFirebase(collection: String): LiveData<List<MultiProject>> {
        TODO("Not yet implemented")
    }

    override fun getMultiProjectUsersFromFirebase(
        project: MultiProject,
        collection: String
    ): LiveData<List<User>> {
        TODO("Not yet implemented")
    }
}
