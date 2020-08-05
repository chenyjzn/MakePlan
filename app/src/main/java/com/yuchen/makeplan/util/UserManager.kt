package com.yuchen.makeplan.util

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.yuchen.makeplan.data.User

object UserManager {
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var user : User

    fun isLogInFun():Boolean{
        return auth.currentUser!=null
    }
}