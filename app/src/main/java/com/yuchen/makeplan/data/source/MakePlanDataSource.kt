package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.Team

interface MakePlanDataSource{
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

    fun getMyMultiProjectsFromFirebase () : LiveData<List<Project>>

    suspend fun addMultiProjectToFirebase (project: Project) : Result<String>

    suspend fun updateMultiProjectToFirebase (project: Project) : Result<String>

    suspend fun removeMultiProjectFromFirebase (id: String) : Result<Boolean>

    fun getAllMultiProjectsFromFirebase () : LiveData<List<Project>>

    suspend fun sendJoinRequestToFirebase (project: Project) : Result<Boolean>

    fun getMultiProjectFromFirebase (project: Project) : LiveData<Project>

    fun getMultiProjectTasksFromFirebase (project: Project) : LiveData<List<Task>>

    suspend fun updateMultiProjectTaskToFirebase (project: Project,task: Task) : Result<Boolean>

    suspend fun removeMultiProjectTaskFromFirebase (project: Project,task: Task) : Result<Boolean>

    suspend fun updateMultiProjectCompleteRateToFirebase (project: Project,completeRate : Int) : Result<Boolean>

    fun getMultiProjectUsersFromFirebase (project: Project) : LiveData<List<User>>

    fun getUsersFromFirebase () : LiveData<List<User>>

    fun getMultiProjectJoinRequsetFromFirebase(project: Project) : LiveData<List<User>>
}