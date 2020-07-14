package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.Result

interface MakePlanDataSource{
    suspend fun insertProject(project: Project)

    suspend fun updateProject(project: Project)

    suspend fun removeProject(project: Project)

    fun getAllProjects(): LiveData<List<Project>>

    suspend fun insertProjectToFireBase(project: Project): Result<String>

    suspend fun updateProjectToFireBase(project: Project)

    suspend fun removeProjectToFireBase(project: Project)

    suspend fun getUserFromFireBase(email: String): Result<User>

    suspend fun firebaseAuthWithGoogle(idToken: String) : Result<FirebaseUser?>
}