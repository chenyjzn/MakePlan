package com.yuchen.makeplan.data.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.MultiTask
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanDataSource
import com.yuchen.makeplan.util.UserManager
import com.yuchen.makeplan.util.UserManager.auth
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object MakePlanRemoteDataSource : MakePlanDataSource {

    const val COLLECTION_USERS = "users"
    const val COLLECTION_MULTI_PROJECTS = "multi_projects"
    const val COLLECTION_PERSONAL_PROJECTS = "personal_projects"

    const val COLLECTION_TASK_LIST = "task_list"

    const val FIELD_MEMBERS = "members"
    const val FIELD_MEMBERS_UID = "membersUid"
    const val FIELD_RECEIVE_UID = "receiveUid"
    const val FIELD_SEND_UID = "sendUid"

    const val USER_NOT_EXIST = 0
    const val USER_INFO_NO_CHANGE = 1
    const val USER_INFO_CHANGE = 2

    override suspend fun insertProject(project: Project) {
        TODO("Not yet implemented")
    }

    override suspend fun updateProject(project: Project) {
        TODO("Not yet implemented")
    }

    override suspend fun removeProject(project: Project) {
        TODO("Not yet implemented")
    }

    override suspend fun searchProject(id: Long): Project? {
        TODO("Not yet implemented")
    }

    override fun getAllProjects(): LiveData<List<Project>> {
        TODO("Not yet implemented")
    }

    override suspend fun removePersonalProjectFromFirebase(id: Long): Result<Boolean> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(firebaseUser.uid)
                .collection(COLLECTION_PERSONAL_PROJECTS)
                .document(id.toString())
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("removePersonalProjectFromFirebase fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>): Result<Boolean> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            val userProjects = FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(firebaseUser.uid)
                .collection(COLLECTION_PERSONAL_PROJECTS)
            for ((i, project) in projects.withIndex()) {
                userProjects
                    .document(project.id.toString())
                    .set(project)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (i == projects.lastIndex) {
                                continuation.resume(Result.Success(true))
                            }
                        } else {
                            task.exception?.let {
                                continuation.resume(Result.Error(it))
                                return@addOnCompleteListener
                            }
                            continuation.resume(Result.Fail("uploadPersonalProjectsToFirebase fail"))
                        }
                    }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun downloadPersonalProjectsFromFirebase(): Result<List<Project>> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(firebaseUser.uid)
                .collection(COLLECTION_PERSONAL_PROJECTS)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var projects: List<Project> = listOf()
                        for (i in task.result?.documents.orEmpty()) {
                            projects = projects + listOf(i.toObject(Project::class.java) ?: Project())
                        }
                        continuation.resume(Result.Success(projects))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("downloadPersonalProjectsFromFirebase fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun checkUserExistInFirebase(): Result<Int> = suspendCoroutine { continuation ->
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { getUserSnapshot ->
                            if (getUserSnapshot.data == null) {
                                continuation.resume(Result.Success(USER_NOT_EXIST))
                            } else if (UserManager.user == getUserSnapshot.toObject(User::class.java)) {
                                continuation.resume(Result.Success(USER_INFO_NO_CHANGE))
                            } else if (UserManager.user != getUserSnapshot.toObject(User::class.java)) {
                                continuation.resume(Result.Success(USER_INFO_CHANGE))
                            } else {
                                continuation.resume(Result.Fail("checkUserExistInFirebase unexpected error"))
                            }
                        }
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("checkUserExistInFirebase fail"))
                    }
                }
        }
    }

    override suspend fun updateUserInfoToUsers(): Result<Boolean> = suspendCoroutine { continuation ->
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(firebaseUser.uid)
                .set(UserManager.user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateUserInfoToFirebaseUsers fail"))
                    }
                }
        }
    }

    @Suppress("LABEL_NAME_CLASH")
    override suspend fun updateUserInfoToMultiProjects(): Result<Boolean> = suspendCoroutine { continuation ->
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .whereArrayContains(FIELD_MEMBERS_UID, firebaseUser.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { snapshot ->
                            if (snapshot.documents.isEmpty()) {
                                continuation.resume(Result.Success(true))
                            } else {
                                for ((index, value) in snapshot.documents.withIndex()) {
                                    val project = value.toObject(MultiProject::class.java)
                                    project?.let {
                                        val members = it.members
                                        val newMembers = members.filter {
                                            it.uid != firebaseUser.uid
                                        }.toMutableList()
                                        newMembers.add(UserManager.user)
                                        val data = hashMapOf(
                                            FIELD_MEMBERS to newMembers
                                        )
                                        FirebaseFirestore.getInstance()
                                            .collection(COLLECTION_MULTI_PROJECTS)
                                            .document(value.id)
                                            .set(data, SetOptions.merge())
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    if (index >= snapshot.documents.lastIndex) {
                                                        continuation.resume(Result.Success(true))
                                                    }
                                                } else {
                                                    task.exception?.let {
                                                        continuation.resume(Result.Error(it))
                                                        return@addOnCompleteListener
                                                    }
                                                    continuation.resume(Result.Fail("updateUserInfoToMultiProjects fail"))
                                                }
                                            }
                                    }
                                }
                            }
                        }
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateUserInfoToMultiProjects fail"))
                    }
                }
        }
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser?> = suspendCoroutine { continuation ->
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(auth.currentUser))
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("firebaseAuthWithGoogle fail"))
                }
            }
    }

    override suspend fun addMultiProject(project: MultiProject): Result<Boolean> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            val document = FirebaseFirestore.getInstance()
                    .collection(COLLECTION_MULTI_PROJECTS)
                    .document()
            project.firebaseId = document.id
            project.membersUid.add(UserManager.user.uid)
            project.members.add(UserManager.user)
            document.set(project).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("addMultiProject fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun updateMultiProject(project: MultiProject): Result<Boolean> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            val document = FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
            document.set(project).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("updateMultiProject fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun removeMultiProject(project: MultiProject): Result<Boolean> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("removeMultiProject fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override fun getAllMultiProjects(): LiveData<List<MultiProject>> {
        val liveData = MutableLiveData<List<MultiProject>>()
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn", "[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val list = mutableListOf<MultiProject>()
                    for (document in snapshot!!) {
                        val project = document.toObject(MultiProject::class.java)
                        list.add(project)
                    }
                    liveData.value = list
                }
        }
        return liveData
    }

    override fun getMultiProject(project: MultiProject): LiveData<MultiProject> {
        val liveData = MutableLiveData<MultiProject>()
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn", "[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val projectLiveData = snapshot?.toObject(MultiProject::class.java)
                    liveData.value = projectLiveData
                }
        }
        return liveData
    }

    override fun getMultiProjectTasks(project: MultiProject): LiveData<List<MultiTask>> {
        val liveData = MutableLiveData<List<MultiTask>>()
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_TASK_LIST)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn", "${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val list = mutableListOf<MultiTask>()
                    for (document in snapshot!!) {
                        val task = document.toObject(MultiTask::class.java)
                        list.add(task)
                    }
                    list.sortBy {
                        it.startTimeMillis
                    }
                    liveData.value = list
                }
        }
        return liveData
    }

    override suspend fun updateMultiProjectTask(project: MultiProject, task: MultiTask): Result<Boolean> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_TASK_LIST)
            if (task.firebaseId.isEmpty()) {
                val document = multiProjectsFirebase.document()
                task.firebaseId = document.id
                document.set(task).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateMultiProjectTask fail"))
                    }
                }
            } else {
                val document = multiProjectsFirebase.document(task.firebaseId)
                document.set(task).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateMultiProjectTask fail"))
                    }
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun removeMultiProjectTask(project: MultiProject, task: MultiTask): Result<Boolean> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_TASK_LIST)
                .document(task.firebaseId)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("removeMultiProjectTask fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun updateMultiProjectCompleteRate(project: MultiProject, completeRate: Int): Result<Boolean> = suspendCoroutine { continuation ->
        auth.currentUser?.let { firebaseUser ->
            val data = hashMapOf("completeRate" to completeRate)
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .set(data, SetOptions.merge())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateMultiProjectCompleteRate fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override fun getAllUsers(): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn", "[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val list = mutableListOf<User>()
                    for (document in snapshot!!) {
                        val user = document.toObject(User::class.java)
                        list.add(user)
                    }
                    liveData.value = list
                }
        }
        return liveData
    }

    override fun getMyMultiProjects(field: String): LiveData<List<MultiProject>> {
        val liveData = MutableLiveData<List<MultiProject>>()
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .whereArrayContains(field, firebaseUser.uid)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn", "[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val list = mutableListOf<MultiProject>()
                    for (document in snapshot!!) {
                        val project = document.toObject(MultiProject::class.java)
                        list.add(project)
                    }
                    liveData.value = list
                }
        }
        return liveData
    }

    @Suppress("LABEL_NAME_CLASH")
    override suspend fun requestUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_MULTI_PROJECTS)
            .document(project.firebaseId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val cloudProject = task.result?.toObject(MultiProject::class.java) ?: project
                    val userList = when (projectField) {
                        FIELD_SEND_UID -> {
                            cloudProject.sendUid
                        }
                        FIELD_RECEIVE_UID -> {
                            cloudProject.receiveUid
                        }
                        else -> throw IllegalArgumentException("Not correct field")
                    }
                    userList.add(user.uid)
                    val data = hashMapOf(
                        projectField to userList
                    )
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_MULTI_PROJECTS)
                        .document(project.firebaseId)
                        .set(data, SetOptions.merge())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(true))
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("requestUserToMultiProject fail"))
                            }
                        }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("requestUserToMultiProject fail"))
                }
            }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    @Suppress("LABEL_NAME_CLASH")
    override suspend fun approveUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> = suspendCoroutine { continuation ->
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_MULTI_PROJECTS)
            .document(project.firebaseId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val cloudProject = task.result?.toObject(MultiProject::class.java) ?: project
                    val requestList = when (projectField) {
                        FIELD_SEND_UID -> {
                            cloudProject.sendUid
                        }
                        FIELD_RECEIVE_UID -> {
                            cloudProject.receiveUid
                        }
                        else -> throw IllegalArgumentException("Not correct field")
                    }
                    val memberUidList = cloudProject.membersUid
                    val memberList = cloudProject.members
                    val newRequestList = requestList.filter { user.uid != it }
                    memberUidList.add(user.uid)
                    memberList.add(user)
                    val data = hashMapOf(
                        projectField to newRequestList,
                        FIELD_MEMBERS_UID to memberUidList,
                        FIELD_MEMBERS to memberList
                    )
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_MULTI_PROJECTS)
                        .document(project.firebaseId)
                        .set(data, SetOptions.merge())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(true))
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("approveUserToMultiProject fail"))
                            }
                        }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("approveUserToMultiProject fail"))
                }
            }
    }

    @Suppress("LABEL_NAME_CLASH")
    override suspend fun cancelUserToMultiProject(project: MultiProject, user: User, projectField: String): Result<Boolean> = suspendCoroutine { continuation ->
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_MULTI_PROJECTS)
            .document(project.firebaseId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val cloudProject = task.result?.toObject(MultiProject::class.java) ?: project
                    val requestList = when (projectField) {
                        FIELD_SEND_UID -> {
                            cloudProject.sendUid
                        }
                        FIELD_RECEIVE_UID -> {
                            cloudProject.receiveUid
                        }
                        else -> throw IllegalArgumentException("Not correct field")
                    }
                    val newList = requestList.filter { it != user.uid }
                    val data = hashMapOf(
                        projectField to newList
                    )
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_MULTI_PROJECTS)
                        .document(project.firebaseId)
                        .set(data, SetOptions.merge())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(true))
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("cancelUserToMultiProject fail"))
                            }
                        }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("cancelUserToMultiProject fail"))
                }
            }
    }

    @Suppress("LABEL_NAME_CLASH")
    override suspend fun removeUserToMultiProject(project: MultiProject, user: User): Result<Boolean> = suspendCoroutine { continuation ->
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_MULTI_PROJECTS)
            .document(project.firebaseId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val cloudProject = task.result?.toObject(MultiProject::class.java) ?: project
                    val memberUidList = cloudProject.membersUid
                    val memberList = cloudProject.members
                    val newMemberUidList = memberUidList.filter { user.uid != it }
                    val newMemberList = memberList.filter { user.uid != it.uid }
                    val data = hashMapOf(
                        FIELD_MEMBERS_UID to newMemberUidList,
                        FIELD_MEMBERS to newMemberList
                    )
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_MULTI_PROJECTS)
                        .document(project.firebaseId)
                        .set(data, SetOptions.merge())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(true))
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("removeUserToMultiProject fail"))
                            }
                        }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("removeUserToMultiProject fail"))
                }
            }
    }

    override fun getMultiProjectUsersUid(project: MultiProject, field: String): LiveData<List<String>> {
        val liveData = MutableLiveData<List<String>>()
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn", "[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val cloudProject = snapshot?.toObject(MultiProject::class.java) ?: project
                    val list = when (field) {
                        FIELD_MEMBERS_UID -> {
                            cloudProject.membersUid
                        }
                        FIELD_RECEIVE_UID -> {
                            cloudProject.receiveUid
                        }
                        FIELD_SEND_UID -> {
                            cloudProject.sendUid
                        }
                        else -> throw IllegalArgumentException("Wrong field name")
                    }
                    liveData.value = list
                }
        }
        return liveData
    }

    override suspend fun getUsersByUidList(uidList: List<String>): Result<List<User>> = suspendCoroutine { continuation ->
        if (uidList.isEmpty())
            continuation.resume(Result.Success(listOf()))
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
        auth.currentUser?.let { firebaseUser ->
            var userList: MutableList<User> = mutableListOf()
            for ((index, value) in uidList.withIndex()) {
                FirebaseFirestore.getInstance()
                    .collection(COLLECTION_USERS)
                    .document(value)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result?.toObject(User::class.java)
                            result?.let {
                                userList.add(it)
                            }
                            if (index >= uidList.lastIndex) {
                                continuation.resume(Result.Success(userList))
                            }
                        } else {
                            task.exception?.let {
                                continuation.resume(Result.Error(it))
                                return@addOnCompleteListener
                            }
                            continuation.resume(Result.Fail("getUsersByUidList fail"))
                        }
                    }
            }
        }
    }
}
