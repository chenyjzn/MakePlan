package com.yuchen.makeplan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource
import com.yuchen.makeplan.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel(private val repository: MakePlanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun getUser(){
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val userResult = repository.updateUserInfoToFirebase()
            when(userResult){
                is Result.Success ->{
                    Log.d("chenyjzn", "getFireBaseUser OK")
                    UserManager.user = userResult.data
                }
                is Result.Error ->{
                    Log.d("chenyjzn", "getFireBaseUser result = ${userResult.exception}")
                }
                is Result.Fail ->{
                    Log.d("chenyjzn", "getFireBaseUser result = ${userResult.error}")
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun signInFirebaseWithGoogle(idToken: String){
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.firebaseAuthWithGoogle(idToken)
            when(result){
                is Result.Success ->{
                    Log.d("chenyjzn", "signInFirebaseWithGoogle OK")
                    val userResult = repository.updateUserInfoToFirebase()
                    when(userResult){
                        is Result.Success ->{
                            Log.d("chenyjzn", "getFireBaseUser OK")
                            UserManager.user = userResult.data
                        }
                        is Result.Error ->{
                            Log.d("chenyjzn", "getFireBaseUser result = ${userResult.exception}")
                        }
                        is Result.Fail ->{
                            Log.d("chenyjzn", "getFireBaseUser result = ${userResult.error}")
                        }
                    }
                }
                is Result.Error ->{
                    Log.d("chenyjzn", "signInFirebaseWithGoogle result = ${result.exception}")
                }
                is Result.Fail ->{
                    Log.d("chenyjzn", "signInFirebaseWithGoogle result = ${result.error}")
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun addDummy(){
        coroutineScope.launch {
            addDummyUser()
        }
    }

    suspend fun addDummyUser(){
        val userFirebase = FirebaseFirestore.getInstance().collection("users")
        val document = userFirebase.document()
        val user = User("GITA", "GITA@gmail.com", "", document.id)
        document.set(user)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}