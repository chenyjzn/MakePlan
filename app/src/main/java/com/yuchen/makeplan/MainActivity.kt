package com.yuchen.makeplan

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.yuchen.makeplan.UserManager.auth
import com.yuchen.makeplan.UserManager.googleSignInClient
import com.yuchen.makeplan.databinding.ActivityMainBinding

const val SECOND_MILLIS = 1000L
const val MINUTE_MILLIS = 60000L
const val HOUR_MILLIS = 3600000L
const val DAY_MILLIS = 86400000L

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

//        this.supportActionBar?.hide()

//        val displayMetrics = DisplayMetrics()
//        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
//        Log.d("chenyjzn","$displayMetrics")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            if (UserManager.isLogIn())
                signOut()
            else
                signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("chenyjzn", "firebaseAuthWithGoogle: ${account.photoUrl}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("chenyjzn", "Google sign in failed", e)
                updateUI(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        binding.mainLoginProgress.visibility=View.VISIBLE

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("chenyjzn", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("chenyjzn", "signInWithCredential:failure", task.exception)

                    val view = binding.mainLayout
                    Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()

                    updateUI(null)
                }

                binding.mainLoginProgress.visibility=View.INVISIBLE

            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        auth.signOut()

        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        binding.mainLoginProgress.visibility=View.INVISIBLE
        if (user != null) {
            UserManager.userName = user.displayName
            UserManager.userEmail = user.email
            UserManager.userPhoto = user.photoUrl
            Log.d("chenyjzn", "user sign in ok : ${user.email}, ${user.displayName}, ${user.photoUrl}")
        } else {
            Log.d("chenyjzn", "user sign in ng: $user")
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
