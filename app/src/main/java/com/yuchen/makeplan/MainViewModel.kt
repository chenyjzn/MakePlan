package com.yuchen.makeplan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MakePlanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun getUser(email:String){
        coroutineScope.launch {
            repository.getUserFromFireBase(email)
        }
    }

    fun updateUser(user: FirebaseUser?) {
        _loadingStatus.value = LoadingStatus.DONE
        if (user != null) {
            UserManager.userName = user.displayName
            UserManager.userEmail = user.email
            UserManager.userPhoto = user.photoUrl
            Log.d("chenyjzn", "user sign in ok : ${user.email}, ${user.displayName}, ${user.photoUrl}")
        } else {
            UserManager.userName = null
            UserManager.userEmail = null
            UserManager.userPhoto = null
            Log.d("chenyjzn", "user sign in ng: $user")
        }
    }

    fun signInFirebaseWithGoogle(idToken: String){
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.firebaseAuthWithGoogle(idToken)
            when(result){
                is Result.Success ->{
                    Log.d("chenyjzn", "signInFirebaseWithGoogle OK")
                    updateUser(result.data)
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}