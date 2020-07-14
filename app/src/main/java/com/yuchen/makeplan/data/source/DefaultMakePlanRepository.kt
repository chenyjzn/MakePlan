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

    override fun getAllProjects(): LiveData<List<Project>> {
        return makePlanLocalDataSource.getAllProjects()
    }

    override suspend fun insertProjectToFireBase(project: Project): Result<String> {
        return makePlanRemoteDataSource.insertProjectToFireBase(project)
    }

    override suspend fun updateProjectToFireBase(project: Project) {
        makePlanRemoteDataSource.updateProjectToFireBase(project)
    }

    override suspend fun removeProjectToFireBase(project: Project) {
        makePlanRemoteDataSource.removeProject(project)
    }

    override suspend fun getUserFromFireBase(email: String): Result<User>{
        return makePlanRemoteDataSource.getUserFromFireBase(email)
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser?> {
        return makePlanRemoteDataSource.firebaseAuthWithGoogle(idToken)
    }
}