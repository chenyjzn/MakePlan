package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.Team
import com.yuchen.makeplan.data.User

class DefaultMakePlanRepository (private val makePlanRemoteDataSource : MakePlanDataSource, private val makePlanLocalDataSource : MakePlanDataSource) : MakePlanRepository {
    override suspend fun insertProject(project: Project) {
        makePlanLocalDataSource.insertProject(project)
    }

    override suspend fun updateProject(project: Project) {
        makePlanLocalDataSource.updateProject(project)
    }

    override suspend fun removeProject(project: Project) {
        makePlanLocalDataSource.removeProject(project)
    }

    override suspend fun searchProject(id: Long): Project? {
        return makePlanLocalDataSource.searchProject(id)
    }

    override fun getAllProjects(): LiveData<List<Project>> {
        return makePlanLocalDataSource.getAllProjects()
    }

    override suspend fun removeProjectFromFirebase(id: Long): Result<Long> {
        return makePlanRemoteDataSource.removeProjectFromFirebase(id)
    }

    override suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>) : Result<Int> {
        return makePlanRemoteDataSource.uploadPersonalProjectsToFirebase(projects)
    }

    override suspend fun downloadPersonalProjectsFromFirebase(): Result<List<Project>> {
        return makePlanRemoteDataSource.downloadPersonalProjectsFromFirebase()
    }

    override suspend fun updateUserInfoToFirebase(): Result<User>{
        return makePlanRemoteDataSource.updateUserInfoToFirebase()
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser?> {
        return makePlanRemoteDataSource.firebaseAuthWithGoogle(idToken)
    }

    override fun getMyMultiProjectsFromFirebase(): LiveData<List<Project>> {
        return makePlanRemoteDataSource.getMyMultiProjectsFromFirebase()
    }

    override suspend fun addMultiProjectToFirebase(project: Project): Result<String> {
        return makePlanRemoteDataSource.addMultiProjectToFirebase(project)
    }

    override suspend fun updateMultiProjectToFirebase(project: Project): Result<String> {
        return makePlanRemoteDataSource.updateMultiProjectToFirebase(project)
    }

    override suspend fun removeMultiProjectFromFirebase(id: String): Result<Boolean> {
        return makePlanRemoteDataSource.removeMultiProjectFromFirebase(id)
    }

    override fun getAllMultiProjectsFromFirebase(): LiveData<List<Project>> {
        return makePlanRemoteDataSource.getAllMultiProjectsFromFirebase()
    }

    override suspend fun sendJoinRequestToFirebase(project: Project): Result<Boolean> {
        return makePlanRemoteDataSource.sendJoinRequestToFirebase(project)
    }

    override fun getMultiProjectFromFirebase(project: Project): LiveData<Project> {
        return makePlanRemoteDataSource.getMultiProjectFromFirebase(project)
    }

    override fun getMultiProjectTasksFromFirebase(project: Project): LiveData<List<Task>> {
        return makePlanRemoteDataSource.getMultiProjectTasksFromFirebase(project)
    }

    override suspend fun updateMultiProjectTaskToFirebase(project: Project, task: Task): Result<Boolean> {
        return makePlanRemoteDataSource.updateMultiProjectTaskToFirebase(project, task)
    }

    override suspend fun removeMultiProjectTaskFromFirebase(project: Project, task: Task): Result<Boolean> {
        return makePlanRemoteDataSource.removeMultiProjectTaskFromFirebase(project,task)
    }

    override suspend fun updateMultiProjectCompleteRateToFirebase(project: Project, completeRate: Int): Result<Boolean> {
        return makePlanRemoteDataSource.updateMultiProjectCompleteRateToFirebase(project, completeRate)
    }

    override fun getMultiProjectUsersFromFirebase(project: Project): LiveData<List<User>> {
        return makePlanRemoteDataSource.getMultiProjectUsersFromFirebase(project)
    }

    override fun getUsersFromFirebase(): LiveData<List<User>> {
        return makePlanRemoteDataSource.getUsersFromFirebase()
    }

    override fun getMultiProjectJoinRequsetFromFirebase(project: Project): LiveData<List<User>> {
        return makePlanRemoteDataSource.getMultiProjectJoinRequsetFromFirebase(project)
    }
}