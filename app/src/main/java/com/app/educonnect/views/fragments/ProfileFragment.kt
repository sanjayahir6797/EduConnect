package com.app.educonnect.views.fragments

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.educonnect.R
import com.app.educonnect.databinding.FragmentHomeBinding
import com.app.educonnect.databinding.FragmentProfileBinding
import com.app.educonnect.utils.Extensions
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        getProfile()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun getProfile(){
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            val emailVerified = user.isEmailVerified
            val uid = user.uid

            if(name!=null) {
                binding.txtName.text = "Name :$name"
            }else{
                binding.txtName.visibility=View.GONE
            }

            if(email!=null) {
                binding.txtEmail.text="Email $email"
            }else{
                binding.txtEmail.visibility=View.GONE
            }


        }
    }
}