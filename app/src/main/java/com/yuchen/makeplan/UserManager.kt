package com.yuchen.makeplan

import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

object UserManager {
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

    var userName : String? = null
    var userEmail : String? = null
    var userPhoto : Uri? = null

    fun isLogIn():Boolean{
        return auth.currentUser!=null
    }
}