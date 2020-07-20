package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.*

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

    fun getMyMultiProjectsFromFirebase () : LiveData<List<MultiProject>>

    suspend fun addMultiProjectToFirebase (project: MultiProject) : Result<Boolean>

    suspend fun updateMultiProjectToFirebase (project: MultiProject) : Result<String>

    suspend fun removeMultiProjectFromFirebase (project: MultiProject) : Result<Boolean>

    fun getAllMultiProjectsFromFirebase () : LiveData<List<MultiProject>>

    suspend fun sendJoinRequestToFirebase (project: MultiProject) : Result<Boolean>

    fun getMultiProjectFromFirebase (project: MultiProject) : LiveData<MultiProject>

    fun getMultiProjectTasksFromFirebase (project: MultiProject) : LiveData<List<MultiTask>>

    suspend fun updateMultiProjectTaskToFirebase (project: MultiProject,task: MultiTask) : Result<Boolean>

    suspend fun removeMultiProjectTaskFromFirebase (project: MultiProject,task: MultiTask) : Result<Boolean>

    suspend fun updateMultiProjectCompleteRateToFirebase (project: MultiProject,completeRate : Int) : Result<Boolean>

    fun getMultiProjectUsersFromFirebase (project: MultiProject) : LiveData<List<User>>

    fun getUsersFromFirebase () : LiveData<List<User>>

    fun getMultiProjectJoinRequestFromFirebase(project: MultiProject) : LiveData<List<User>>

    suspend fun updateMultiProjectUsersToFirebase(project: MultiProject, users :List<User>) : Result<Boolean>

    suspend fun removeMultiProjectUsersFromFirebase(project: MultiProject, user :User) : Result<Boolean>

    suspend fun multiProjectInviteUser(project: MultiProject, user :User) : Result<Boolean>

    fun getMultiProjectInviteRequestFromFirebase(project: MultiProject) : LiveData<List<User>>

    suspend fun multiProjectCancelInviteFromFirebase(project: MultiProject, user :User) : Result<Boolean>
}