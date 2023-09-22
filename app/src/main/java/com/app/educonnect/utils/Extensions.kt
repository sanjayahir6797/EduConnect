package com.app.educonnect.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.educonnect.models.User
import com.app.educonnect.utils.FirebaseUtils.database
import com.app.educonnect.utils.FirebaseUtils.firebaseUser
import com.google.firebase.auth.FirebaseAuth


object Extensions {
    const val SHARED_PREFS = "shared_prefs"
   fun Activity.toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    fun Fragment.toast(msg: String){
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }

     fun addUserToDatabase(name:String, email:String, uid:String) {
        // use real
        database.child("user").child(uid).setValue(User(encodeString(name), encodeString(email), uid)) // add user to database
         //database.child("Users").child(uid).child("email").setValue(email);
    }

    fun encodeString(string: String?): String? {
        return string?.replace(".", ",")
    }

    fun decodeString(string: String?): String? {
        return string?.replace(",", ".")
    }

    fun sharedPreference(activity: Activity,email: String){
        val sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("isLogin", true)
        editor.putString("email", email)
        editor.apply()
    }

    fun isLogin(activity: Activity):Boolean{
        val sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
      return  sharedPreferences.getBoolean("isLogin",false)
    }
    fun getSharedPreferenceUser(activity: Activity):String{
        val sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        return  sharedPreferences.getString("email","")!!
    }
    fun clearPreferences(activity: Activity){
        val sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
       editor.clear()
        editor.apply()
    }
     fun getUserName(): String? {
        //val user = FirebaseAuth.currentUser
        return if (firebaseUser != null) {
            firebaseUser.displayName
        } else ""
    }

}