package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.*

class DefaultMakePlanRepository (private val makePlanRemoteDataSource: MakePlanDataSource, private val makePlanLocalDataSource: MakePlanDataSource) : MakePlanRepository {
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

    override suspend fun removePersonalProjectFromFirebase(id: Long): Result<Boolean> {
        return makePlanRemoteDataSource.removePersonalProjectFromFirebase(id)
    }

    override suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>) : Result<Boolean> {
        return makePlanRemoteDataSource.uploadPersonalProjectsToFirebase(projects)
    }

    override suspend fun downloadPersonalProjectsFromFirebase(): Result<List<Project>> {
        return makePlanRemoteDataSource.downloadPersonalProjectsFromFirebase()
    }

    override suspend fun checkUserExistInFirebase(): Result<Int> {
        return makePlanRemoteDataSource.checkUserExistInFirebase()
    }

    override suspend fun updateUserInfoToUsers(): Result<Boolean> {
        return makePlanRemoteDataSource.updateUserInfoToUsers()
    }

    override suspend fun updateUserInfoToMultiProjects(): Result<Boolean> {
        return makePlanRemoteDataSource.updateUserInfoToMultiProjects()
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser?> {
        return makePlanRemoteDataSource.firebaseAuthWithGoogle(idToken)
    }

    override fun getAllMultiProjects(): LiveData<List<MultiProject>> {
        return makePlanRemoteDataSource.getAllMultiProjects()
    }

    override fun getMultiProject(project: MultiProject): LiveData<MultiProject> {
        return makePlanRemoteDataSource.getMultiProject(project)
    }

    override fun getMultiProjectTasks(project: MultiProject): LiveData<List<MultiTask>> {
        return makePlanRemoteDataSource.getMultiProjectTasks(project)
    }

    override suspend fun updateMultiProjectTask(project: MultiProject, task: MultiTask): Result<Boolean> {
        return makePlanRemoteDataSource.updateMultiProjectTask(project, task)
    }

    override suspend fun removeMultiProjectTask(project: MultiProject, task: MultiTask): Result<Boolean> {
        return makePlanRemoteDataSource.removeMultiProjectTask(project, task)
    }

    override suspend fun updateMultiProjectCompleteRate(project: MultiProject, completeRate: Int): Result<Boolean> {
        return makePlanRemoteDataSource.updateMultiProjectCompleteRate(project, completeRate)
    }

    override fun getAllUsers(): LiveData<List<User>> {
        return makePlanRemoteDataSource.getAllUsers()
    }

    override suspend fun addMultiProject(project: MultiProject): Result<Boolean> {
        return makePlanRemoteDataSource.addMultiProject(project)
    }

    override suspend fun updateMultiProject(project: MultiProject): Result<Boolean> {
        return makePlanRemoteDataSource.updateMultiProject(project)
    }

    override suspend fun removeMultiProject(project: MultiProject): Result<Boolean> {
        return makePlanRemoteDataSource.removeMultiProject(project)
    }

    override fun getMyMultiProjects(field : String): LiveData<List<MultiProject>> {
        return makePlanRemoteDataSource.getMyMultiProjects(field)
    }

    override suspend fun requestUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> {
        return makePlanRemoteDataSource.requestUserToMultiProject(project, user, projectField)
    }

    override suspend fun approveUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> {
        return makePlanRemoteDataSource.approveUserToMultiProject(project, user, projectField)
    }

    override suspend fun cancelUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> {
        return makePlanRemoteDataSource.cancelUserToMultiProject(project, user, projectField)
    }

    override suspend fun removeUserToMultiProject(project: MultiProject, user: User): Result<Boolean> {
        return makePlanRemoteDataSource.removeUserToMultiProject(project, user)
    }

    override fun getMultiProjectUsersUid(project: MultiProject, field: String): LiveData<List<String>> {
        return makePlanRemoteDataSource.getMultiProjectUsersUid(project, field)
    }

    override suspend fun getUsersByUidList(uidList: List<String>): Result<List<User>> {
        return makePlanRemoteDataSource.getUsersByUidList(uidList)
    }
}