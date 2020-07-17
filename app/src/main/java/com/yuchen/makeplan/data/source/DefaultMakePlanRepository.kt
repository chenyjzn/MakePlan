package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.Project
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

    override suspend fun addTeamToFirebase(teamName : String): Result<String> {
        return makePlanRemoteDataSource.addTeamToFirebase(teamName)
    }

    override fun getUserTeamsFromFirebase(): MutableLiveData<List<Team>> {
        return makePlanRemoteDataSource.getUserTeamsFromFirebase()
    }

    override fun getAllTeamsFromFirebase(): MutableLiveData<List<Team>> {
        return makePlanRemoteDataSource.getAllTeamsFromFirebase()
    }

    override suspend fun getTeamByTextFromFirebase(text: String): Result<List<Team>> {
        return makePlanRemoteDataSource.getTeamByTextFromFirebase(text)
    }

    override suspend fun createTeamToFirebase(teamName: String): Result<String> {
        return makePlanRemoteDataSource.createTeamToFirebase(teamName)
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
}