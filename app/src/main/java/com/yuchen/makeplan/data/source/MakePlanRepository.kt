package com.yuchen.makeplan.data.source

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.*

interface MakePlanRepository {
    suspend fun insertProject(project: Project)

    suspend fun updateProject(project: Project)

    suspend fun removeProject(project: Project)

    suspend fun searchProject(id: Long): Project?

    fun getAllProjects(): LiveData<List<Project>>

    suspend fun removePersonalProjectFromFirebase(id: Long): Result<Boolean>

    suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>): Result<Boolean>

    suspend fun downloadPersonalProjectsFromFirebase(): Result<List<Project>>

    suspend fun checkUserExistInFirebase(): Result<Int>

    suspend fun updateUserInfoToUsers(): Result<Boolean>

    suspend fun updateUserInfoToMultiProjects(): Result<Boolean>

    suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser?>

    fun getAllMultiProjects(): LiveData<List<MultiProject>>

    fun getMultiProject(project: MultiProject): LiveData<MultiProject>

    fun getMultiProjectTasks(project: MultiProject): LiveData<List<MultiTask>>

    suspend fun updateMultiProjectTask(project: MultiProject, task: MultiTask): Result<Boolean>

    suspend fun removeMultiProjectTask(project: MultiProject, task: MultiTask): Result<Boolean>

    suspend fun updateMultiProjectCompleteRate(project: MultiProject, completeRate: Int): Result<Boolean>

    fun getAllUsers(): LiveData<List<User>>

    suspend fun addMultiProject(project: MultiProject): Result<Boolean>

    suspend fun updateMultiProject(project: MultiProject): Result<Boolean>

    suspend fun removeMultiProject(project: MultiProject): Result<Boolean>

    fun getMyMultiProjects(field: String): LiveData<List<MultiProject>>

    suspend fun requestUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean>

    suspend fun approveUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean>

    suspend fun cancelUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean>

    suspend fun removeUserToMultiProject(project: MultiProject, user: User): Result<Boolean>

    fun getMultiProjectUsersUid(project: MultiProject, field: String): LiveData<List<String>>

    suspend fun getUsersByUidList(uidList: List<String>): Result<List<User>>
}