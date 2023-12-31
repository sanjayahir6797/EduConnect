package com.app.educonnect.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.educonnect.databinding.ActivityMainBinding
import com.app.educonnect.utils.Extensions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        if (Firebase.auth.currentUser != null) {
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
        }
        setContentView(view)
       // val isLogin = Extensions.isLogin(this)


        binding.btnSignInEmailPassword.setOnClickListener {
            startActivity(Intent(this, EmailAuthActivity::class.java))
        }

        binding.btnSignInGmail.setOnClickListener {
            startActivity(Intent(this, GoogleAuthActivity::class.java))
        }

        binding.btnSignInOtp.setOnClickListener {
            startActivity(Intent(this, PhoneAuthActivity::class.java))
        }
    }

}