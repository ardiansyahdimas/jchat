package com.jumpa.jchat.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jumpa.jchat.adapter.MessageAdapter
import com.jumpa.jchat.data.ModelMessage
import com.jumpa.jchat.data.ModelUser
import com.jumpa.jchat.databinding.ActivityChatBinding
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import com.google.firebase.storage.StorageReference

import com.google.firebase.storage.FirebaseStorage

import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.UploadTask

import com.google.android.gms.tasks.OnFailureListener
import com.jumpa.jchat.R
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var adapter: MessageAdapter
    var receiverRoom: String? = null
    var senderRoom: String? = null
    private var imageUri: Uri? = null
    private var senderIdImage = ""

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    companion object{
        const val EXTRA_DATA = "extra_data"
        const val REQUEST_IMAGE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val dataUser = intent.getParcelableExtra<ModelUser>(EXTRA_DATA)
        showDataChat(dataUser)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showDataChat(dataUser: ModelUser?) {
        dataUser?.let {
            title = dataUser.name
            val receiverUid = dataUser.uid
            val senderUid = FirebaseAuth.getInstance().currentUser?.uid
            if (senderUid != null) {
                senderIdImage = senderUid
            }
            senderRoom = receiverUid + senderUid
            receiverRoom = senderUid + receiverUid
            getListMessage(senderUid)
            sendMessage(senderUid)
            uploadImage()
        }
    }

    private fun sendMessage(senderUid: String?){
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString().trim()
            val messageObject = ModelMessage(message, senderUid, Date().time)
            if(binding.messageEditText.text.isEmpty()){
                binding.messageEditText.error = "ketika pesan kamu"
            } else {
                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
            }
            binding.messageEditText.setText("")
        }
    }

    private fun getListMessage(senderUid: String?){
        val manager = LinearLayoutManager(this)
        binding.messageRecyclerView.itemAnimator = null
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        val messageRef =   mDbRef.child("chats").child(senderRoom!!).child("messages")
        val options = FirebaseRecyclerOptions.Builder<ModelMessage>()
            .setQuery(messageRef, ModelMessage::class.java)
            .build()
        adapter = MessageAdapter(options, senderUid)
        binding.messageRecyclerView.adapter = adapter
        messageRef.addValueEventListener(object :  ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val array = adapter.itemCount
                if(array > 0) {
                    binding.messageRecyclerView.smoothScrollToPosition(array - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    private fun uploadImage(){
        binding.addImage.setOnClickListener {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                val openGalleryIntent = Intent(Intent.ACTION_PICK)
                openGalleryIntent.type = "image/*"
                resultLauncher.launch(openGalleryIntent)
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Izinkan Aplikasi Mengakses Storage?",
                    REQUEST_IMAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            val ref = storageReference.child("images/" + UUID.randomUUID().toString())
            binding.messageRecyclerView.loadSkeleton(R.layout.item_message) { itemCount(10) }
            ref.putFile(imageUri!!).addOnSuccessListener {taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {uri ->
                    val messageObject = ModelMessage(uri.toString(), senderIdImage, Date().time)
                    mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener {
                            mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                                .setValue(messageObject)
                        }
                    binding.messageRecyclerView.hideSkeleton()
                }

            }
        }
    }
}