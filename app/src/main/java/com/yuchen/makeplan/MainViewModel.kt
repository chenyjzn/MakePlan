package com.yuchen.makeplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.FIELD_SEND_UID
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.USER_INFO_CHANGE
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource.USER_NOT_EXIST
import com.yuchen.makeplan.util.UserManager
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

    val notifyCount = repository.getMyMultiProjects(FIELD_SEND_UID)

    fun checkAndUpdateUser(firebaseUser: FirebaseUser){
        UserManager.user = User(
            firebaseUser.displayName ?: "",
            firebaseUser.email ?: "",
            firebaseUser.photoUrl.toString(),
            firebaseUser.uid
        )
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            when(val checkUserResult = repository.checkUserExistInFirebase()){
                is Result.Success ->{
                    if (checkUserResult.data == USER_NOT_EXIST || checkUserResult.data == USER_INFO_CHANGE){
                        when(val updateUserResult = repository.updateUserInfoToUsers()){
                            is Result.Success ->{
                                if (checkUserResult.data == USER_INFO_CHANGE){
                                    when(val updateProjectsResult = repository.updateUserInfoToMultiProjects()){
                                        is Result.Success ->{

                                        }
                                        is Result.Error -> {
                                            _loadingStatus.value = LoadingStatus.ERROR("${updateProjectsResult.exception}")
                                        }
                                        is Result.Fail -> {
                                            _loadingStatus.value = LoadingStatus.ERROR(updateProjectsResult.error)
                                        }
                                    }
                                }
                            }
                            is Result.Error -> {
                                _loadingStatus.value = LoadingStatus.ERROR("${updateUserResult.exception}")
                            }
                            is Result.Fail -> {
                                _loadingStatus.value = LoadingStatus.ERROR(updateUserResult.error)
                            }
                        }
                    }
                }
                is Result.Error -> {
                    _loadingStatus.value = LoadingStatus.ERROR("${checkUserResult.exception}")
                }
                is Result.Fail -> {
                    _loadingStatus.value = LoadingStatus.ERROR(checkUserResult.error)
                }
            }
            _loadingStatus.value = LoadingStatus.DONE
        }
    }

    fun signInFirebaseWithGoogle(idToken: String) {
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            when (val firebaseAuthResult = repository.firebaseAuthWithGoogle(idToken)) {
                is Result.Success -> {
                    val firebaseUser = firebaseAuthResult.data
                    if (firebaseUser != null) {
                        UserManager.user = User(
                            firebaseUser.displayName ?: "",
                            firebaseUser.email ?: "",
                            firebaseUser.photoUrl.toString(),
                            firebaseUser.uid
                        )
                        when (val checkUserResult = repository.checkUserExistInFirebase()) {
                            is Result.Success -> {
                                if (checkUserResult.data == USER_NOT_EXIST || checkUserResult.data == USER_INFO_CHANGE) {
                                    when (val updateUserResult =
                                        repository.updateUserInfoToUsers()) {
                                        is Result.Success -> {
                                            if (checkUserResult.data == USER_INFO_CHANGE) {
                                                when (val updateProjectsResult =
                                                    repository.updateUserInfoToMultiProjects()) {
                                                    is Result.Success -> {

                                                    }
                                                    is Result.Error -> {
                                                        _loadingStatus.value = LoadingStatus.ERROR("${updateProjectsResult.exception}")
                                                    }
                                                    is Result.Fail -> {
                                                        _loadingStatus.value = LoadingStatus.ERROR(updateProjectsResult.error)
                                                    }
                                                }
                                            }
                                        }
                                        is Result.Error -> {
                                            _loadingStatus.value = LoadingStatus.ERROR("${updateUserResult.exception}")
                                        }
                                        is Result.Fail -> {
                                            _loadingStatus.value = LoadingStatus.ERROR(updateUserResult.error)
                                        }
                                    }
                                }
                            }
                            is Result.Error -> {
                                _loadingStatus.value = LoadingStatus.ERROR("${checkUserResult.exception}")
                            }
                            is Result.Fail -> {
                                _loadingStatus.value = LoadingStatus.ERROR(checkUserResult.error)
                            }
                        }
                    }
                }
                is Result.Error -> {
                    _loadingStatus.value = LoadingStatus.ERROR("${firebaseAuthResult.exception}")
                }
                is Result.Fail -> {
                    _loadingStatus.value = LoadingStatus.ERROR(firebaseAuthResult.error)
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