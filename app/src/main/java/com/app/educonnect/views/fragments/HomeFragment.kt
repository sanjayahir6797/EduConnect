package com.app.educonnect.views.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.educonnect.R
import com.app.educonnect.databinding.FragmentHomeBinding
import com.app.educonnect.utils.Extensions.toast
import com.app.educonnect.utils.FirebaseUtils.fireStoreDatabase
import com.app.educonnect.utils.FirebaseUtils.storageReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.OnProgressListener
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.btnChooseImage.setOnClickListener {
            launchGallery()
        }
        binding.btnShowFirestoreList.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeToList()
            findNavController().navigate(directions)
        }


        binding.btnSaveFirestore.setOnClickListener {

            if (TextUtils.isEmpty(binding.etTitle.text)) {
                binding.etTitle.error = getString(R.string.msg_enter_title_name)
            } else if (TextUtils.isEmpty(binding.etDescription.text)) {
                binding.etDescription.error = getString(R.string.msg_enter_description)
            } else {
                uploadImage()
                addDataToFireStore(
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString(),

                    )
            }
        }

        return binding.root
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST
        )
    }

    private fun addDataToFireStore(
        title: String, description: String

    ) {

        val dbCourses: CollectionReference = fireStoreDatabase.collection("Courses")
        val data = HashMap<String, Any>()
        data["title"] = title
        data["description"] = description
        data["imageUrl"] = filePath.toString()
        dbCourses.add(data).addOnSuccessListener {
            binding.etTitle.setText("")
            binding.etDescription.setText("")
            toast(getString(R.string.msg_firestore_success))
        }.addOnFailureListener { e ->
                toast(
                    "Fail to add data \n$e"
                )
            }
    }

    private fun uploadImage() {
        if (filePath != null) {

            val progressDialog = ProgressDialog(requireActivity())
            progressDialog.setTitle(getString(R.string.msg_uploading))
            progressDialog.show()

            val ref = storageReference.child(
                    "images/" + UUID.randomUUID().toString()
                )

            ref.putFile(filePath!!).addOnSuccessListener { // Image uploaded successfully
                    progressDialog.dismiss()
                    toast("Image Uploaded!!")
                }.addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    toast("Failed " + e.message)
                }.addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot>,
                    com.google.firebase.storage.OnProgressListener<UploadTask.TaskSnapshot> {
                    override fun onProgress(
                        taskSnapshot: UploadTask.TaskSnapshot
                    ) {
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage(
                            "Uploaded " + progress.toInt() + "%"
                        )
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            try {
                binding.imgPreview.visibility = View.VISIBLE
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)
                binding.imgPreview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}