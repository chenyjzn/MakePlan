package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.Project
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
}