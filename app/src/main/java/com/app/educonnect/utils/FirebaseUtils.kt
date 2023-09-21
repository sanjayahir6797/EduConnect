package com.app.educonnect.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseUtils {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    val fireStoreDatabase = FirebaseFirestore.getInstance()
    val storageReference = FirebaseStorage.getInstance().reference
    val database= FirebaseDatabase.getInstance().reference


}