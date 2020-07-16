package com.yuchen.makeplan.teams

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.Team
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TeamsViewModel (private val repository: MakePlanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>>
        get() = _teams

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun getUserTeamLiveData() {
        _teams = repository.getUserTeamsFromFirebase()
        _loadingStatus.value = LoadingStatus.DONE

    }

    init {
        getUserTeamLiveData()
    }

    fun addTeamToFirebase(teamName : String){
        coroutineScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.addTeamToFirebase(teamName)
            when(result){
                is Result.Success ->{
                    Log.d("chenyjzn", "addTeamToFirebase OK")
                }
                is Result.Error ->{
                    Log.d("chenyjzn", "addTeamToFirebase result = ${result.exception}")
                }
                is Result.Fail ->{
                    Log.d("chenyjzn", "addTeamToFirebase result = ${result.error}")
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