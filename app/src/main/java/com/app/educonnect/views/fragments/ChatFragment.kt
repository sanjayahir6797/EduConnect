package com.app.educonnect.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.educonnect.R
import com.app.educonnect.databinding.FragmentChatBinding
import com.app.educonnect.models.User
import com.app.educonnect.utils.Extensions.toast
import com.app.educonnect.utils.FirebaseUtils.database
import com.app.educonnect.utils.FirebaseUtils.firebaseAuth
import com.app.educonnect.views.adapters.UserAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class ChatFragment : Fragment() {
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var binding: FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(inflater, container, false)
        userList = ArrayList()
        binding.progressBar.visibility=View.VISIBLE

        // realtime database load
        database.child("user").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar.visibility=View.GONE
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (firebaseAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                if (userList.size > 0) {
                    binding.rvChatList.layoutManager = LinearLayoutManager(requireActivity())

                    adapter = UserAdapter(requireActivity(), userList)
                    binding.rvChatList.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    toast(getString(R.string.msg_no_users_found))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility=View.GONE
                toast(error.message)
            }
        })
        return binding.root
    }


}