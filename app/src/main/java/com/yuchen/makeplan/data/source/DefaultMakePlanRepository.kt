package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.*

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

    override fun getMyMultiProjectsFromFirebase(): LiveData<List<MultiProject>> {
        return makePlanRemoteDataSource.getMyMultiProjectsFromFirebase()
    }

    override suspend fun addMultiProjectToFirebase(project: MultiProject): Result<Boolean> {
        return makePlanRemoteDataSource.addMultiProjectToFirebase(project)
    }

    override suspend fun updateMultiProjectToFirebase(project: MultiProject): Result<String> {
        return makePlanRemoteDataSource.updateMultiProjectToFirebase(project)
    }

    override suspend fun removeMultiProjectFromFirebase(project: MultiProject): Result<Boolean> {
        return makePlanRemoteDataSource.removeMultiProjectFromFirebase(project)
    }

    override fun getAllMultiProjectsFromFirebase(): LiveData<List<MultiProject>> {
        return makePlanRemoteDataSource.getAllMultiProjectsFromFirebase()
    }

    override suspend fun sendJoinRequestToFirebase(project: MultiProject): Result<Boolean> {
        return makePlanRemoteDataSource.sendJoinRequestToFirebase(project)
    }

    override fun getMultiProjectFromFirebase(project: MultiProject): LiveData<MultiProject> {
        return makePlanRemoteDataSource.getMultiProjectFromFirebase(project)
    }

    override fun getMultiProjectTasksFromFirebase(project: MultiProject): LiveData<List<MultiTask>> {
        return makePlanRemoteDataSource.getMultiProjectTasksFromFirebase(project)
    }

    override suspend fun updateMultiProjectTaskToFirebase(project: MultiProject, task: MultiTask): Result<Boolean> {
        return makePlanRemoteDataSource.updateMultiProjectTaskToFirebase(project, task)
    }

    override suspend fun removeMultiProjectTaskFromFirebase(project: MultiProject, task: MultiTask): Result<Boolean> {
        return makePlanRemoteDataSource.removeMultiProjectTaskFromFirebase(project,task)
    }

    override suspend fun updateMultiProjectCompleteRateToFirebase(project: MultiProject, completeRate: Int): Result<Boolean> {
        return makePlanRemoteDataSource.updateMultiProjectCompleteRateToFirebase(project, completeRate)
    }

    override fun getMultiProjectUsersFromFirebase(project: MultiProject): LiveData<List<User>> {
        return makePlanRemoteDataSource.getMultiProjectUsersFromFirebase(project)
    }

    override fun getUsersFromFirebase(): LiveData<List<User>> {
        return makePlanRemoteDataSource.getUsersFromFirebase()
    }

    override fun getMultiProjectJoinRequestFromFirebase(project: MultiProject): LiveData<List<User>> {
        return makePlanRemoteDataSource.getMultiProjectJoinRequestFromFirebase(project)
    }

    override suspend fun updateMultiProjectUsersToFirebase(project: MultiProject, users: List<User>): Result<Boolean> {
        return makePlanRemoteDataSource.updateMultiProjectUsersToFirebase(project,users)
    }

    override suspend fun removeMultiProjectUsersFromFirebase(
        project: MultiProject,
        user: User
    ): Result<Boolean> {
        return makePlanRemoteDataSource.removeMultiProjectUsersFromFirebase(project,user)
    }

    override suspend fun multiProjectInviteUser(
        project: MultiProject,
        user: User
    ): Result<Boolean> {
        return makePlanRemoteDataSource.multiProjectInviteUser(project,user)
    }

    override fun getMultiProjectInviteRequestFromFirebase(project: MultiProject): LiveData<List<User>> {
        return makePlanRemoteDataSource.getMultiProjectInviteRequestFromFirebase(project)
    }

    override suspend fun multiProjectCancelInviteFromFirebase(
        project: MultiProject,
        user: User
    ): Result<Boolean> {
        return makePlanRemoteDataSource.multiProjectCancelInviteFromFirebase(project,user)
    }

    override suspend fun multiProjectConfirmUserJoinFirebase(
        project: MultiProject,
        user: User
    ): Result<Boolean> {
        return makePlanRemoteDataSource.multiProjectConfirmUserJoinFirebase(project, user)
    }
}