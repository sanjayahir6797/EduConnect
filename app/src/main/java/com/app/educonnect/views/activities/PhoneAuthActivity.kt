package com.app.educonnect.views.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.educonnect.R
import com.app.educonnect.databinding.ActivityPhoneAuthBinding
import com.app.educonnect.utils.Extensions
import com.app.educonnect.utils.Extensions.addUserToDatabase
import com.app.educonnect.utils.Extensions.toast
import com.app.educonnect.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneAuthBinding
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.title_otp)
        actionbar.setDisplayHomeAsUpEnabled(true)
        binding.btnGetOtp.setOnClickListener {
            if (TextUtils.isEmpty(binding.etPhoneNumber.text.toString())) {
                toast(getString(R.string.msg_enter_valid_phone_number))
            } else {
                binding.progressBar.visibility = View.VISIBLE
                val phone = "+91" + binding.etPhoneNumber.text.toString()
                startPhoneNumberVerification(phone)
            }
        }
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                val code = credential.smsCode;
                binding.progressBar.visibility = View.GONE
                if (code != null) {
                    binding.etOtp.setText(code)
                    verifyPhoneNumberWithCode(storedVerificationId, code)
                }

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                binding.progressBar.visibility = View.GONE
                if (e is FirebaseAuthInvalidCredentialsException) {
                    toast(e.message.toString())
                } else if (e is FirebaseTooManyRequestsException) {
                    toast(e.message.toString())
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    toast(e.message.toString())
                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        binding.btnVerify.setOnClickListener {
            if (TextUtils.isEmpty(binding.etOtp.text.toString())) {
                toast(getString(R.string.msg_enter_the_otp))
            } else {
                binding.progressBar.visibility = View.VISIBLE
                verifyPhoneNumberWithCode(storedVerificationId, binding.etOtp.text.toString());
            }
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    toast("signInWithCredential")
                    binding.progressBar.visibility = View.GONE
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        val name = user.displayName
                        val email = user.email
                        val photoUrl = user.photoUrl
                        val number = user.phoneNumber
                        val emailVerified = user.isEmailVerified
                        val uid = user.uid
                        addUserToDatabase(number ?: "", "", uid)
                        Extensions.sharedPreference(this, number.toString())
                    }
                    val intent = Intent(this@PhoneAuthActivity, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }
                }
            }


    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }
}