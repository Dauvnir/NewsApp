package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityChangeEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class ChangeEmailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChangeEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button2.setOnClickListener {
            val intent = Intent(this, ConnectedToDatabaseActivity::class.java)
            startActivity(intent)
        }
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val emailPattern: Regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        binding.loginBtn.setOnClickListener {
            val newEmail = binding.rePasswordInput.text.toString()

            if(newEmail.isEmpty()){
                Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!newEmail.matches(emailPattern)){
                Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (user == null) {
                Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.loginBtn.isEnabled = false
                user.verifyBeforeUpdateEmail(newEmail)
                    .addOnCompleteListener{task ->
                        binding.loginBtn.isEnabled = true
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Verification email sent to $newEmail", Toast.LENGTH_SHORT).show()
                        }else{
                            val exception = task.exception
                            when (exception) {
                                is FirebaseAuthUserCollisionException -> {
                                    Toast.makeText(this, "Error: Email already in use.", Toast.LENGTH_SHORT).show()
                                }
                                is FirebaseAuthRecentLoginRequiredException -> {
                                    Toast.makeText(this, "Re-auth your account.", Toast.LENGTH_SHORT).show()
                                    firebaseAuth.signOut()
                                    val intent = Intent(this, SignUpActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    Toast.makeText(this, "Error updating email.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

        }
    }
}