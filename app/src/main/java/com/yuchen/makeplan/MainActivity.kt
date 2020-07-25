package com.yuchen.makeplan

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.yuchen.makeplan.databinding.ActivityMainBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.util.UserManager
import com.yuchen.makeplan.util.UserManager.auth
import com.yuchen.makeplan.util.UserManager.googleSignInClient
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

const val SECOND_MILLIS = 1000L
const val MINUTE_MILLIS = 60000L
const val HOUR_MILLIS = 3600000L
const val DAY_MILLIS = 86400000L

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

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
            if (UserManager.isLogIn()) {
                signOut()
            }
            else
                signIn()
        }

        viewModel.loadingStatus.observe(this, Observer {
            it?.let {
                when(it){
                    LoadingStatus.LOADING ->{
                        binding.mainLoginProgress.visibility = View.VISIBLE
                    }
                    LoadingStatus.DONE -> {
                        binding.mainLoginProgress.visibility = View.INVISIBLE
                    }
                    LoadingStatus.ERROR -> {
                        binding.mainLoginProgress.visibility = View.INVISIBLE
                    }
                }
            }
        })

        findNavController(R.id.nav_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.projectsFragment ->{
                    this.actionBar?.hide()
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.multiProjectsFragment ->{
                    this.actionBar?.hide()
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.notifyFragment ->{
                    this.actionBar?.hide()
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.multiFragment ->{
                    this.actionBar?.hide()
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                else ->{
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_personal ->{
                    findNavController(R.id.nav_fragment).navigate(NavigationDirections.actionGlobalProjectsFragment())
                }
                R.id.nav_team -> {
                    findNavController(R.id.nav_fragment).navigate(NavigationDirections.actionGlobalMultiProjectsFragment())
                }
                R.id.nav_notify ->{
                    findNavController(R.id.nav_fragment).navigate(NavigationDirections.actionGlobalNotifyFragment())
                }
            }
            true
        }

        binding.imageView.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.imageView3.setOnClickListener {
            dispatchGetPictureIntent()
        }
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            viewModel.getUser()
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
                Log.w("chenyjzn", "Google sign in failed", e)
            }
        }
        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
        }
        if (requestCode == GET_PICTURE && resultCode == RESULT_OK) {
            Log.d("chenyjzn","Get Pic ${data?.data}")
            val selectedImage: Uri? = data?.data
            Glide.with(binding.imageView3.context)
                .load(selectedImage)
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_broken_image_black_24dp)
                        .error(R.drawable.ic_broken_image_black_24dp)
                )
                .into(binding.imageView3)
        }
    }
    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {

        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAKE_PICTURE = 1
        private const val GET_PICTURE = 2
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, TAKE_PICTURE)
            }
        }
    }

    private fun dispatchGetPictureIntent() {
        Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { getPictureIntent ->
            getPictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(getPictureIntent, GET_PICTURE)
            }
        }
    }
}