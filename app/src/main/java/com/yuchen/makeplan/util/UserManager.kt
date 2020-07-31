package com.yuchen.makeplan.util

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.data.User

object UserManager {
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var user : User

    val loginUser = MutableLiveData<User>()

    fun isLogInFun():Boolean{
        return auth.currentUser!=null
    }

}