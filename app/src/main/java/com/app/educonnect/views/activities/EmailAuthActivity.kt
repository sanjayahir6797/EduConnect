package com.app.educonnect.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.app.educonnect.R
import com.app.educonnect.databinding.ActivityEmailAuthBinding
import com.app.educonnect.utils.Extensions.addUserToDatabase
import com.app.educonnect.utils.Extensions.sharedPreference
import com.app.educonnect.utils.Extensions.toast
import com.app.educonnect.utils.FirebaseUtils
import com.app.educonnect.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.auth.FirebaseAuth
class EmailAuthActivity : AppCompatActivity() {
    private lateinit var signInEmail: String
    private lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>
    private lateinit var binding: ActivityEmailAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEmailAuthBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.title_email_authentication)
        actionbar.setDisplayHomeAsUpEnabled(true)
        signInInputsArray = arrayOf(binding.etSignInEmail, binding.etSignInPassword)

        binding.btnSignIn.setOnClickListener {
            signInUser()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signInUser() {
        binding.progressBar.visibility = View.VISIBLE;
        signInEmail = binding.etSignInEmail.text.toString().trim()
        signInPassword = binding.etSignInPassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        binding.progressBar.visibility = View.GONE
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.let {
                            val name = user.displayName
                            val email = user.email
                            val photoUrl = user.photoUrl
                            val emailVerified = user.isEmailVerified
                            val uid = user.uid
                            addUserToDatabase(signInEmail?:"",email?:"",uid)
                            sharedPreference(this, signInEmail.toString())
                        }

                        toast(getString(R.string.msg_success_login))
                        val intent = Intent(this@EmailAuthActivity, HomeActivity::class.java)
                        finish()
                        startActivity(intent)
                    } else {
                        binding.progressBar.visibility = View.GONE
                        toast(getString(R.string.msg_failed_login))
                    }
                }
        } else {
            binding.progressBar.visibility = View.GONE
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }
}