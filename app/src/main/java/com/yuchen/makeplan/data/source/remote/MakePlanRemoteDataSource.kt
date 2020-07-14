package com.yuchen.makeplan.data.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.UserManager
import com.yuchen.makeplan.UserManager.auth
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object MakePlanRemoteDataSource :MakePlanDataSource {

    private const val PATH_PROJECTS = "projects"
    private const val PATH_USERS = "users"

    override suspend fun insertProject(project: Project) {
        TODO("Not yet implemented")
    }

    override suspend fun updateProject(project: Project) {
        TODO("Not yet implemented")
    }

    override suspend fun removeProject(project: Project) {
        TODO("Not yet implemented")
    }

    override fun getAllProjects(): LiveData<List<Project>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertProjectToFireBase(project: Project): Result<String> = suspendCoroutine {continuation ->
        val projectFirebase = FirebaseFirestore.getInstance().collection(PATH_PROJECTS)
        val document = projectFirebase.document()
        project.fireBaseId = document.id
        document.set(project).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(document.id))
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("insertProjectToFireBase fail"))
                }
            }
    }

    override suspend fun updateProjectToFireBase(project: Project) {
    }

    override suspend fun removeProjectToFireBase(project: Project) {
    }

    override suspend fun getUserFromFireBase(email: String): Result<User> = suspendCoroutine{continuation ->
        val userFirebase = FirebaseFirestore.getInstance().collection(PATH_USERS)
        userFirebase
            .whereEqualTo("email", email).get()
            .addOnCompleteListener {task ->
            if (task.isSuccessful) {
                Log.d("chenyjzn","check user ${task.result?.documents}")
                task.result?.let {
                    //沒有 User 的話直接創建, 有 User 取得 User
                    if (it.documents.isEmpty()){
                        val document = userFirebase.document()
                        val newUser = User(UserManager.userName,UserManager.userEmail,UserManager.userPhoto,document.id, null, null)
                        document.set(newUser).addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(newUser))
                            } else {
                                task.exception?.let {
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(Result.Fail("setUserToFireBase fail"))
                            }
                        }
                    } else{
                        val user = it.documents[0].toObject(User::class.java)
                        user?.let {
                            continuation.resume(Result.Success(it))
                        }
                    }
                }
            } else {
                task.exception?.let {
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail("getUserFromFireBase fail"))
            }
        }
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
}