package com.jumpa.jchat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.jumpa.jchat.databinding.ActivityLoginBinding
import com.jumpa.jchat.pref.SharedPref
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = SharedPref(this)
        mAuth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        signIn()
    }

    private fun signIn(){
        binding.btnLogin.setOnClickListener {
            val edtEmail = binding.edtEmail.text.toString().trim()
            val edtPassword = binding.edtPassword.text.toString().trim()
            when{
                edtEmail.isEmpty() -> {
                    binding.edtEmail.error = "enter email address"
                    binding.edtEmail.requestFocus()
                }
                !isValidEmail(edtEmail) ->{
                    binding.edtEmail.error = "invalid email address"
                    binding.edtEmail.requestFocus()
                }
                edtPassword.isEmpty() -> {
                    binding.edtPassword.error = "enter password"
                    binding.edtPassword.requestFocus()
                }
                else -> {
                    binding.containerLogin.loadSkeleton()
                    mAuth.signInWithEmailAndPassword(edtEmail, edtPassword)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                finish()
                                pref.setLoggin(true)
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            } else {
                                Toast.makeText(this, "user does not exist", Toast.LENGTH_SHORT).show()
                            }
                            binding.containerLogin.hideSkeleton()
                        }
                }
            }

        }
    }
    
    private fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}