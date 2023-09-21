package com.app.educonnect.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.educonnect.R
import com.app.educonnect.databinding.FragmentFireStoreListBinding
import com.app.educonnect.models.ItemsViewModel
import com.app.educonnect.utils.Extensions.toast
import com.app.educonnect.utils.FirebaseUtils
import com.app.educonnect.views.adapters.ListAdapter


class FireStoreListFragment : Fragment() {
    private lateinit var binding: FragmentFireStoreListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFireStoreListBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE
        getFireStoreList()
        return binding.root
    }

    private fun getFireStoreList() {
        val list = arrayListOf<ItemsViewModel>()

        FirebaseUtils.fireStoreDatabase.collection("Courses")
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    val img = document["imageUrl"].toString()
                    val name = document["title"].toString()
                    val desc = document["description"].toString()
                    val course = ItemsViewModel(name = name, desc = desc, image = img)
                    list.add(course)

                }
                binding.progressBar.visibility = View.GONE
                if (list.size > 0) {
                    binding.rvFireStoreList.layoutManager = LinearLayoutManager(requireActivity())
                    val adapter = ListAdapter(list)
                    binding.rvFireStoreList.adapter = adapter

                } else {
                    binding.progressBar.visibility = View.GONE
                    toast(getString(R.string.msg_no_data_found))
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                it.printStackTrace()
                toast(getString(R.string.msg_fail_to_get_firestore_data))
            }
    }
}