package com.yuchen.makeplan.data.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.*
import com.yuchen.makeplan.util.UserManager
import com.yuchen.makeplan.util.UserManager.auth
import com.yuchen.makeplan.data.source.MakePlanDataSource
import com.yuchen.makeplan.util.UserManager.user
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object MakePlanRemoteDataSource :MakePlanDataSource {

    const val COLLECTION_USERS = "users"
    const val COLLECTION_MEMBERS = "members"
    const val COLLECTION_MULTI_PROJECTS = "multi_projects"
    const val COLLECTION_PERSONAL_PROJECTS = "personal_projects"


    const val COLLECTION_JOIN_REQUEST = "join_request"
    const val COLLECTION_SEND_REQUEST = "send_request"


    const val COLLECTION_INVITE_REQUEST = "invite_request"
    const val COLLECTION_RECEIVE_REQUEST = "receive_request"

    const val COLLECTION_TASK_LIST = "task_list"

    const val FIELD_MEMBERS = "members"
    const val FIELD_MEMBERSUID = "membersUid"

    const val PERSONAL_TEAMS = "personal_teams"
    const val PATH_TEAMS = "teams"
    const val TEAM_MEMBERS = "team_member"

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

    override suspend fun removeProjectFromFirebase(id: Long): Result<Long>  = suspendCoroutine { continuation ->
        auth.currentUser?.let {firebaseUser ->
            val userProjects = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(firebaseUser.uid).collection(COLLECTION_PERSONAL_PROJECTS)
            userProjects.document(id.toString()).delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(id))
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("removeProjectFromFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not loginn"))
    }

    override suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>) : Result<Int> = suspendCoroutine { continuation ->
        auth.currentUser?.let {firebaseUser ->
            val userProjects = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(firebaseUser.uid).collection(COLLECTION_PERSONAL_PROJECTS)
            var uploadSuccessCount = 0
            for ( (i,project) in projects.withIndex()){
                userProjects.document(project.id.toString()).set(project).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        uploadSuccessCount += 1
                        if (i == projects.lastIndex)
                            continuation.resume(Result.Success(uploadSuccessCount))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("uploadProjectsToFirebase fail"))
                    }
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun downloadPersonalProjectsFromFirebase(): Result<List<Project>> = suspendCoroutine {continuation ->
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(firebaseUser.uid).collection(COLLECTION_PERSONAL_PROJECTS)
                .get().addOnCompleteListener{task ->
                    if (task.isSuccessful) {
                        var projects : List<Project> = listOf()
                        for (i in task.result?.documents.orEmpty()){
                            projects = projects + listOf(i.toObject(Project::class.java)?:Project())
                        }
                        continuation.resume(Result.Success(projects))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("uploadProjectsToFirebase fail"))
                    }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun updateUserInfoToFirebase(): Result<User> = suspendCoroutine{ continuation ->
        auth.currentUser?.let {firebaseUser ->
            val userFirebase = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
            val user = User(firebaseUser.displayName?:"", firebaseUser.email?:"", firebaseUser.photoUrl.toString(), firebaseUser.uid)
            userFirebase.document(firebaseUser.uid).set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                            continuation.resume(Result.Success(user))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("getUserFromFireBase fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String) : Result<FirebaseUser?> = suspendCoroutine { continuation->
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

    override fun getMyMultiProjectsFromFirebase(): LiveData<List<MultiProject>> {
        val liveData = MutableLiveData<List<MultiProject>>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS)
                .whereArrayContains(FIELD_MEMBERSUID,firebaseUser.uid)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
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

    override suspend fun addMultiProjectToFirebase(project: MultiProject): Result<Boolean>  = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS)
            val document = multiProjectsFirebase.document()
            project.firebaseId = document.id
            project.membersUid.add(UserManager.user.uid)
            project.members.add(UserManager.user)
            document.set(project).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    document.collection(COLLECTION_MEMBERS)
                        .document(user.uid)
                        .set(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(UserManager.user.uid).collection(COLLECTION_MULTI_PROJECTS).document(project.firebaseId)
                                    .set(project).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        continuation.resume(Result.Success(true))
                                    } else {
                                        task.exception?.let {
                                            continuation.resume(Result.Error(it))
                                            return@addOnCompleteListener
                                        }
                                        continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                                    }
                                }
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                            }
                        }

                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("addMultiProjectToFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun updateMultiProjectToFirebase(project: MultiProject): Result<String> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS)
            val document = multiProjectsFirebase.document(project.firebaseId)
            document.set(project).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(document.id))
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun removeMultiProjectFromFirebase(project: MultiProject): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS)
            val document = multiProjectsFirebase.document(project.firebaseId)
            document.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override fun getAllMultiProjectsFromFirebase(): LiveData<List<MultiProject>> {
        val liveData = MutableLiveData<List<MultiProject>>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
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

    override suspend fun sendJoinRequestToFirebase(project: MultiProject): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_JOIN_REQUEST)
            val document = multiProjectsFirebase.document(UserManager.user.uid)
            document.set(UserManager.user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_USERS)
                        .document(firebaseUser.uid)
                        .collection(COLLECTION_SEND_REQUEST)
                        .document(project.firebaseId).set(project).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Result.Success(true))
                        } else {
                            task.exception?.let {
                                continuation.resume(Result.Error(it))
                                return@addOnCompleteListener
                            }
                            continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                        }
                    }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("sendJoinRequestToFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override fun getMultiProjectFromFirebase(project: MultiProject): LiveData<MultiProject> {
        val liveData = MutableLiveData<MultiProject>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val projectLiveData = snapshot?.toObject(MultiProject::class.java)
                    liveData.value = projectLiveData
                }
        }
        return liveData
    }

    override fun getMultiProjectTasksFromFirebase(project: MultiProject): LiveData<List<MultiTask>> {
        val liveData = MutableLiveData<List<MultiTask>>()
        auth.currentUser?.let { firebaseUser ->
            FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId).collection(COLLECTION_TASK_LIST)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val list = mutableListOf<MultiTask>()
                    for (document in snapshot!!) {
                        val task = document.toObject(MultiTask::class.java)
                        list.add(task)
                    }
                    liveData.value = list
                }
        }
        return liveData
    }

    override suspend fun updateMultiProjectTaskToFirebase(project: MultiProject,task: MultiTask) : Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_TASK_LIST)
            if (task.firebaseId.isEmpty()){
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
                        continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                    }
                }
            }else{
                val document = multiProjectsFirebase.document(task.firebaseId)
                document.set(task).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                    }
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun removeMultiProjectTaskFromFirebase(project: MultiProject, task: MultiTask): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS).document(project.firebaseId)
                .collection(COLLECTION_TASK_LIST)
                .document(task.firebaseId)
                .delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun updateMultiProjectCompleteRateToFirebase(project: MultiProject, completeRate: Int): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val data = hashMapOf("completeRate" to completeRate)
            val multiProjectsFirebase = FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .set(data, SetOptions.merge()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                    }
                }
            }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override fun getMultiProjectUsersFromFirebase(project: MultiProject): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance().collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_MEMBERS)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
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

    override fun getUsersFromFirebase(): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
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

    override fun getMultiProjectJoinRequestFromFirebase(project: MultiProject): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(project.firebaseId)
                .collection(COLLECTION_JOIN_REQUEST)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
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

    override suspend fun updateMultiProjectUsersToFirebase(project: MultiProject, users: List<User>): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val uids = users.map {
                it.uid
            }
            val data =
                hashMapOf(
                    FIELD_MEMBERS to users,
                    FIELD_MEMBERSUID to uids)
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .set(data, SetOptions.merge()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun removeMultiProjectUsersFromFirebase(project: MultiProject, user: User): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId).collection(COLLECTION_MEMBERS).document(user.uid)
                .delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun multiProjectInviteUser(project: MultiProject, user: User): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_INVITE_REQUEST)
            val document = multiProjectsFirebase.document(user.uid)
            document.set(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_USERS)
                        .document(user.uid)
                        .collection(COLLECTION_RECEIVE_REQUEST)
                        .document(project.firebaseId).set(project).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(true))
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                            }
                        }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("sendJoinRequestToFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override fun getMultiProjectInviteRequestFromFirebase(project: MultiProject): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_INVITE_REQUEST)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
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

    override suspend fun multiProjectCancelInviteFromFirebase(project: MultiProject, user: User): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_INVITE_REQUEST)
            val document = multiProjectsFirebase.document(user.uid)
            document.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_USERS)
                        .document(user.uid)
                        .collection(COLLECTION_RECEIVE_REQUEST)
                        .document(project.firebaseId).delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(true))
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                            }
                        }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("sendJoinRequestToFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override suspend fun multiProjectConfirmUserJoinFirebase(project: MultiProject, user: User): Result<Boolean> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val multiProjectsFirebase = FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(COLLECTION_JOIN_REQUEST)
            val document = multiProjectsFirebase.document(user.uid)
            document.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_USERS)
                        .document(user.uid)
                        .collection(COLLECTION_SEND_REQUEST)
                        .document(project.firebaseId).delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val multiProjectsFirebase = FirebaseFirestore.getInstance()
                                    .collection(COLLECTION_MULTI_PROJECTS)
                                    .document(project.firebaseId)
                                    .collection(COLLECTION_MEMBERS)
                                val document = multiProjectsFirebase.document(user.uid)
                                document.set(user).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        FirebaseFirestore.getInstance()
                                            .collection(COLLECTION_USERS)
                                            .document(user.uid)
                                            .collection(COLLECTION_MULTI_PROJECTS)
                                            .document(project.firebaseId).set(project).addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    continuation.resume(Result.Success(true))
                                                } else {
                                                    task.exception?.let {
                                                        continuation.resume(Result.Error(it))
                                                        return@addOnCompleteListener
                                                    }
                                                    continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                                                }
                                            }
                                    } else {
                                        task.exception?.let {
                                            continuation.resume(Result.Error(it))
                                            return@addOnCompleteListener
                                        }
                                        continuation.resume(Result.Fail("sendJoinRequestToFirebase fail"))
                                    }
                                }
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("updateMultiProjectToFirebase fail"))
                            }
                        }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("sendJoinRequestToFirebase fail"))
                }
            }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("User not login"))
    }

    override fun getMyMultiProjectsFromFirebase(collection: String): LiveData<List<MultiProject>> {
        val liveData = MutableLiveData<List<MultiProject>>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(UserManager.user.uid)
                .collection(collection)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
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

    override fun getMultiProjectUsersFromFirebase(project: MultiProject, collection: String): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance()
                .collection(COLLECTION_MULTI_PROJECTS)
                .document(project.firebaseId)
                .collection(collection)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
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
}
