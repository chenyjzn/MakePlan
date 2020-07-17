package com.yuchen.makeplan.teams

import android.os.UserManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.Result
import com.yuchen.makeplan.data.Team
import com.yuchen.makeplan.data.source.MakePlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TeamsViewModel (private val repository: MakePlanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var _myTeams = MutableLiveData<List<Team>>()
    val myTeams: LiveData<List<Team>>
        get() = _myTeams

    private var _allTeams = MutableLiveData<List<Team>>()
    val allTeams: LiveData<List<Team>>
        get() = _allTeams

    private var _searchTeams = MutableLiveData<List<Team>>()
    val searchTeams: LiveData<List<Team>>
        get() = _searchTeams

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    fun getUserTeamLiveData() {
        _myTeams = repository.getUserTeamsFromFirebase()
        _loadingStatus.value = LoadingStatus.DONE

    }

    fun getAllTeamLiveData() {
        _allTeams = repository.getUserTeamsFromFirebase()
        _loadingStatus.value = LoadingStatus.DONE
    }

    init {
        getUserTeamLiveData()
        getAllTeamLiveData()
    }

    fun resetMyTeam(){
        _myTeams.value = _myTeams.value
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

    fun findTeamsByText(text : String?){
        text?.let {text->
            val searchList = _allTeams.value?.filter {team->
                (team.name.contains(text)||team.leader.email.contains(text)||team.leader.displayName.contains(text))
            }
            _searchTeams.value = searchList
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}