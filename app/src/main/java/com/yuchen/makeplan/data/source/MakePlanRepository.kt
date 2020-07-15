package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.Result

interface MakePlanRepository {
    suspend fun insertProject(project: Project)

    suspend fun updateProject(project: Project)

    suspend fun removeProject(project: Project)

    suspend fun searchProject(id: Long) : Project?

    fun getAllProjects(): LiveData<List<Project>>

    suspend fun removeProjectFromFirebase(id: Long): Result<Long>

    suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>) : Result<Int>

    suspend fun downloadPersonalProjectsFromFirebase() : Result<List<Project>>

    suspend fun updateUserInfoToFirebase(): Result<User>

    suspend fun firebaseAuthWithGoogle(idToken: String) : Result<FirebaseUser?>

}