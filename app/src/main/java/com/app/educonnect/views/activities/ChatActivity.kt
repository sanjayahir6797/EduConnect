package com.app.educonnect.views.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.educonnect.R
import com.app.educonnect.databinding.ActivityChatBinding
import com.app.educonnect.models.Message
import com.app.educonnect.utils.Extensions.toast
import com.app.educonnect.utils.FirebaseUtils.database
import com.app.educonnect.views.adapters.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    var receiverRoom: String? = null
    var senderRoom: String? = null
    var senderUid: String? = null

    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        actionbar!!.title = "Chat"
        actionbar.setDisplayHomeAsUpEnabled(true)
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

         senderUid = FirebaseAuth.getInstance().currentUser?.uid
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        if (name == "") {
            supportActionBar?.title = "Chat"
        } else {
            supportActionBar?.title = name
        }

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        binding.rvChatConversion.layoutManager = LinearLayoutManager(this)
        binding.rvChatConversion.adapter = messageAdapter

        // logic for adding data to recyclerView
        database.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        binding.etMessageBox.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                return@OnEditorActionListener true
            }
            false
        })
    }

    fun sendMessage(){
        val message = binding.etMessageBox.text.toString()
        if (message == "") {
            toast(getString(R.string.enter_the_msg))
        } else {
            val messageObject = Message(message, senderUid)
            database.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    database.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            binding.etMessageBox.setText("")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}