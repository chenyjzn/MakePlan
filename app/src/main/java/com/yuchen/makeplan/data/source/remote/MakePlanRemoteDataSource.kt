package com.yuchen.makeplan.data.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.util.UserManager
import com.yuchen.makeplan.util.UserManager.auth
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Team
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object MakePlanRemoteDataSource :MakePlanDataSource {

    private const val PERSONAL_PROJECTS = "personal_projects"
    private const val PERSONAL_TEAMS = "personal_teams"
    private const val PATH_USERS = "users"
    private const val PATH_TEAMS = "teams"

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
            val userProjects = FirebaseFirestore.getInstance().collection(PATH_USERS).document(firebaseUser.uid).collection(PERSONAL_PROJECTS)
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
            continuation.resume(Result.Fail("removeProjectFromFirebase fail, User not login"))
    }

    override suspend fun uploadPersonalProjectsToFirebase(projects: List<Project>) : Result<Int> = suspendCoroutine { continuation ->
        auth.currentUser?.let {firebaseUser ->
            val userProjects = FirebaseFirestore.getInstance().collection(PATH_USERS).document(firebaseUser.uid).collection(PERSONAL_PROJECTS)
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
            continuation.resume(Result.Fail("uploadProjectsToFirebase fail, User not login"))
    }

    override suspend fun downloadPersonalProjectsFromFirebase(): Result<List<Project>> = suspendCoroutine {continuation ->
        auth.currentUser?.let {firebaseUser ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(firebaseUser.uid).collection(PERSONAL_PROJECTS)
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
            continuation.resume(Result.Fail("uploadProjectsToFirebase fail, User not login"))
    }

    override suspend fun updateUserInfoToFirebase(): Result<User> = suspendCoroutine{ continuation ->
        auth.currentUser?.let {firebaseUser ->
            val userFirebase = FirebaseFirestore.getInstance().collection(PATH_USERS)
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
            continuation.resume(Result.Fail("getUserFromFireBase fail, User not login"))
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

    override suspend fun addTeamToFirebase(teamName : String): Result<String> = suspendCoroutine { continuation->
        auth.currentUser?.let {firebaseUser ->
            val teamFirebase = FirebaseFirestore.getInstance().collection(PATH_TEAMS)
            val document = teamFirebase.document()
            val newTeam = Team(teamName,document.id,System.currentTimeMillis(),UserManager.user)
            document.set(newTeam).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userFirebase = FirebaseFirestore.getInstance().collection(PATH_USERS).document(firebaseUser.uid).collection(PERSONAL_TEAMS)
                        userFirebase.document(document.id).set(newTeam).addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                continuation.resume(Result.Success(document.id))
                            } else {
                                task1.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("firebaseAuthWithGoogle fail"))
                            }
                        }
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("addTeamToFirebase fail"))
                    }
                }
        }
        if (auth.currentUser == null)
            continuation.resume(Result.Fail("addTeamToFirebase fail, User not login"))
    }

    override fun getUserTeamsFromFirebase(): MutableLiveData<List<Team>> {
        val liveData = MutableLiveData<List<Team>>()
        auth.currentUser?.let {firebaseUser ->
            val userFirebase = FirebaseFirestore.getInstance().collection(PATH_USERS)
                .document(firebaseUser.uid).collection(PERSONAL_TEAMS)
                .addSnapshotListener { snapshot, exception ->
                    exception?.let {
                        Log.d("chenyjzn","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    Log.d("chenyjzn","User team snapshotListener")
                    val list = mutableListOf<Team>()
                    for (document in snapshot!!) {
                        val team = document.toObject(Team::class.java)
                        list.add(team)
                    }
                    liveData.value = list
                }
            }
        return liveData
    }

}