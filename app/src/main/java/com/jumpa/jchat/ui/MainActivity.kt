package com.jumpa.jchat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jumpa.jchat.data.ModelUser
import com.jumpa.jchat.R
import com.jumpa.jchat.adapter.UserAdapter
import com.jumpa.jchat.databinding.ActivityMainBinding
import com.jumpa.jchat.pref.SharedPref
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var pref: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        pref = SharedPref(this)
        getListUser()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.sign_out){
            mAuth.signOut()
            pref.removeData()
            finish()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return true
        }
        return true
    }

    private fun getListUser(){
        val userAdapter = UserAdapter()
        userAdapter.onItemClick = { selectedData ->
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_DATA, selectedData)
            startActivity(intent)
        }
        binding.rvUsers.loadSkeleton(R.layout.item_user) { itemCount(10) }
        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(ModelUser::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid) {
                        binding.rvUsers.hideSkeleton()
                        userAdapter.setData(currentUser)
                    } else {
                        title = currentUser?.name
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        with(binding.rvUsers) {
            layoutManager =   LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = userAdapter
        }
    }
}