package com.app.educonnect.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.educonnect.R
import com.app.educonnect.databinding.ActivityGoogleAuthBinding
import com.app.educonnect.utils.Extensions
import com.app.educonnect.utils.Extensions.addUserToDatabase
import com.app.educonnect.utils.Extensions.toast
import com.app.educonnect.utils.FirebaseUtils.firebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoogleAuthBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.title_gmail)
        actionbar.setDisplayHomeAsUpEnabled(true)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignIn.setOnClickListener {
            signIn()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                toast("Google sign in failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    Log.d(TAG, "signInWithCredential:success")
                    toast("signInWithCredential:success")
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        // Name, email address, and profile photo Url
                        val name = user.displayName
                        val email = user.email
                        val photoUrl = user.photoUrl
                        val emailVerified = user.isEmailVerified
                        val uid = user.uid
                        addUserToDatabase(name ?: "", email ?: "", uid)
                        Extensions.sharedPreference(this, name.toString())
                    }
                    val intent = Intent(this@GoogleAuthActivity, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
            }
    }

    private fun signIn() {
        binding.progressBar.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}