package com.yuchen.makeplan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.yuchen.makeplan.databinding.ActivityMainBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.ext.toPx
import com.yuchen.makeplan.util.UserManager
import com.yuchen.makeplan.util.UserManager.auth
import com.yuchen.makeplan.util.UserManager.googleSignInClient

const val BUTTON_CLICK_TRAN = 500L
const val MINUTE_MILLIS = 60000L
const val HOUR_MILLIS = 3600000L
const val DAY_MILLIS = 86400000L

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels { getVmFactory() }

    fun showProgress(){
        binding.mainLoginProgress.visibility = View.VISIBLE
    }

    fun hideProgress(){
        binding.mainLoginProgress.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findNavController(R.id.nav_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.projectsFragment,
                R.id.multiProjectsFragment,
                R.id.notifyFragment,
                R.id.multiEditDialog,
                R.id.editDialog -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_personal -> {
                    findNavController(R.id.nav_fragment).navigate(NavigationDirections.actionGlobalProjectsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_team -> {
                    if (UserManager.isLogInFun()) {
                        findNavController(R.id.nav_fragment).navigate(NavigationDirections.actionGlobalMultiProjectsFragment())
                        return@setOnNavigationItemSelectedListener true
                    } else {
                        findNavController(R.id.nav_fragment).navigate(NavigationDirections.actionGlobalLoginDialog())
                        return@setOnNavigationItemSelectedListener false
                    }
                }
                R.id.nav_notify -> {
                    if (UserManager.isLogInFun()) {
                        findNavController(R.id.nav_fragment).navigate(NavigationDirections.actionGlobalNotifyFragment())
                        return@setOnNavigationItemSelectedListener true
                    } else {
                        findNavController(R.id.nav_fragment).navigate(NavigationDirections.actionGlobalLoginDialog())
                        return@setOnNavigationItemSelectedListener false
                    }
                }
            }
            true
        }

        val badge = setNotifyBadge()

        viewModel.notifyCount.observe(this, Observer {
            if (it == null) badge.isVisible = false
            it?.let {
                if (it.isEmpty()){
                    badge.isVisible = false
                }else{
                    badge.isVisible = true
                    badge.number = it.size
                }
            }
        })

        viewModel.loadingStatus.observe(this, Observer {
            when (it) {
                is LoadingStatus.LOADING -> {
                    showProgress()
                    disableTouch()
                }
                is LoadingStatus.DONE -> {
                    enableTouch()
                    hideProgress()
                }
                is LoadingStatus.ERROR -> {
                    showErrorMessage(it.message)
                }
            }
        })

        binding.signInButton.setOnClickListener {
            if (UserManager.isLogInFun()) {
                signOut()
            } else
                signIn()
        }

    }

    private fun setNotifyBadge(): BadgeDrawable {
        val badge = binding.bottomNavigationView.getOrCreateBadge(R.id.nav_notify)
        badge.backgroundColor = resources.getColor(R.color.yellow_400)
        badge.badgeTextColor = resources.getColor(R.color.blue_gray_900)
        badge.verticalOffset = 3.toPx()
        badge.horizontalOffset = 3.toPx()
        badge.isVisible = false
        return badge
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            viewModel.checkAndUpdateUser(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.signInFirebaseWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                showErrorMessage(e.toString())
            }
        }
    }

    private fun disableTouch(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun enableTouch(){
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun showErrorMessage(message: String){
        Snackbar
            .make(binding.mainLayout, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(R.color.yellow_400))
            .setTextColor(resources.getColor(R.color.blue_gray_900))
            .show()
    }

    fun signIn() {
        if (viewModel.loadingStatus.value !is LoadingStatus.LOADING && viewModel.loadingStatus.value !is LoadingStatus.ERROR) {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {

        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

}