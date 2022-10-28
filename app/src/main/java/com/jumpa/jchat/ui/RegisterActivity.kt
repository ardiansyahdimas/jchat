package com.jumpa.jchat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jumpa.jchat.data.ModelUser
import com.jumpa.jchat.databinding.ActivityRegisterBinding
import com.jumpa.jchat.pref.SharedPref

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var radioButton: RadioButton
    private var genderSelected = ""
    private lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Sign Up"
        pref = SharedPref(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mAuth = FirebaseAuth.getInstance()
        binding.radioGroup.setOnCheckedChangeListener{group, checkedId ->
            val intSelectButton: Int = binding.radioGroup.checkedRadioButtonId
            radioButton = findViewById(intSelectButton)
           genderSelected = radioButton.text.toString()
        }
        signUp()
    }


    private fun signUp(){
        binding.btnRegister.setOnClickListener {
            val edtName = binding.edtName.text.toString().trim()
            val edtEmail = binding.edtEmail.text.toString().trim()
            val edtPassword = binding.edtPassword.text.toString().trim()
            when{
                edtName.isEmpty() -> {
                    binding.edtName.error = "enter your name"
                    binding.edtName.requestFocus()
                }
                edtEmail.isEmpty() -> {
                    binding.edtEmail.error = "enter email address"
                    binding.edtEmail.requestFocus()
                }
                !isValidEmail(edtEmail) ->{
                    binding.edtEmail.error = "invalid email address"
                    binding.edtEmail.requestFocus()
                }
                genderSelected == "" -> {
                    Toast.makeText(this, "select gender", Toast.LENGTH_SHORT).show()
                }
                edtPassword.isEmpty() -> {
                    binding.edtPassword.error = "enter password"
                    binding.edtPassword.requestFocus()
                }
                edtPassword.length < 7 -> {
                    binding.edtPassword.error = "7 characters minimum"
                    binding.edtPassword.requestFocus()
                }
                else -> {
                    mAuth.createUserWithEmailAndPassword(edtEmail, edtPassword)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                addUserToDatabase(edtName, edtEmail, mAuth.currentUser!!.uid, genderSelected)
                                finish()
                                pref.setLoggin(true)
                                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            } else {
                                Toast.makeText(this, "some error occurred", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, gender: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(ModelUser(name, email, uid, gender))
    }
    private fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}