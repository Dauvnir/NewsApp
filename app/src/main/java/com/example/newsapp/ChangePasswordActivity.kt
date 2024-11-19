package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

class ChangePasswordActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button2.setOnClickListener {
            val intent = Intent(this, ConnectedToDatabaseActivity::class.java)
            startActivity(intent)
        }
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val passwordPattern: Regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{8,20}$".toRegex()

        binding.loginBtn.setOnClickListener {
            val newPassword = binding.passwordInput.text.toString()
            val rePassword = binding.rePasswordInput.text.toString()
            if(newPassword.isEmpty() || rePassword.isEmpty()){
                Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!newPassword.matches(passwordPattern)){
                Toast.makeText(this, "Password needs uppercase, lowercase, number, and special character.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(newPassword != rePassword){
                Toast.makeText(this, "Passwords needs to be same.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (user == null) {
                Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.loginBtn.isEnabled = false
            user.updatePassword(rePassword)
                .addOnCompleteListener{task ->
                    binding.loginBtn.isEnabled = true
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                    }else{
                        val exception = task.exception
                        when (exception) {
                            is FirebaseAuthRecentLoginRequiredException -> {
                                Toast.makeText(this, "Re-auth your account.", Toast.LENGTH_SHORT).show()
                                firebaseAuth.signOut()
                                val intent = Intent(this, SignUpActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Toast.makeText(this, "Error updating password.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

        }
    }
}