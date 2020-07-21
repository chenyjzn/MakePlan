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


    fun getMyMultiProjects () : LiveData<List<MultiProject>>

    fun getAllMultiProjects () : LiveData<List<MultiProject>>

    fun getMultiProject (project: MultiProject) : LiveData<MultiProject>

    fun getMultiProjectTasks (project: MultiProject) : LiveData<List<MultiTask>>

    suspend fun updateMultiProjectTask (project: MultiProject, task: MultiTask) : Result<Boolean>

    suspend fun removeMultiProjectTask (project: MultiProject, task: MultiTask) : Result<Boolean>

    suspend fun updateMultiProjectCompleteRate (project: MultiProject, completeRate : Int) : Result<Boolean>

    fun getMultiProjectUsers (project: MultiProject) : LiveData<List<User>>

    fun getAllUsers () : LiveData<List<User>>

    suspend fun updateMultiProjectUsers(project: MultiProject, users :List<User>) : Result<Boolean>

    suspend fun addMultiProject (project: MultiProject) : Result<Boolean>

    suspend fun updateMultiProject (project: MultiProject) : Result<String>

    suspend fun removeMultiProject (project: MultiProject) : Result<Boolean>

    fun getMyMultiProjects(field : String) : LiveData<List<MultiProject>>

    fun getMultiProjectUsers(project: MultiProject, collection: String): LiveData<List<User>>

    suspend fun approveUserToMultiProject(project: MultiProject, user: User, projectCollection: String, userCollection:String) : Result<Boolean>

    suspend fun cancelUserToMultiProject(project: MultiProject, user: User, projectCollection: String, userCollection:String) : Result<Boolean>

    suspend fun requestUserToMultiProject(project: MultiProject, user: User, projectCollection: String, userCollection:String) : Result<Boolean>

    suspend fun removeMultiProjectUser(project: MultiProject, user :User) : Result<Boolean>

    suspend fun requestUserToMultiProject(project: MultiProject, user: User, projectField: String) : Result<Boolean>

    suspend fun approveUserToMultiProject(project: MultiProject, user: User, projectField: String) : Result<Boolean>

    suspend fun cancelUserToMultiProject(project: MultiProject, user :User, projectField: String) : Result<Boolean>

    suspend fun removeUserToMultiProject(project: MultiProject, user :User) : Result<Boolean>
}