package com.example.newsapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityDeleteAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

class DeleteAccountActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDeleteAccountBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button2.setOnClickListener {
            val intent = Intent(this, ConnectedToDatabaseActivity::class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("YourPreferenceName", MODE_PRIVATE)
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        binding.loginBtn.setOnClickListener {
            val delete = binding.deletefield.text.toString()
            val match = "Delete"
            if(delete.isEmpty()){
                Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(delete != match){
                Toast.makeText(this, "Provide to text field 'Delete'", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (user == null) {
                Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.loginBtn.isEnabled = false

            user.delete()
                .addOnCompleteListener{task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account deleted.", Toast.LENGTH_SHORT).show()

                        val editor = sharedPreferences.edit()
                        editor.remove("LANGUAGES")
                        editor.remove("CATEGORY")
                        editor.remove("COUNTRY")
                        editor.putBoolean("AUTO_LOGIN", false)
                        editor.apply()

                        FirebaseAuth.getInstance().signOut()

                        val intent = Intent(this, SignUpActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }else{
                        binding.loginBtn.isEnabled = true
                        val exception = task.exception
                        when (exception) {
                            is FirebaseAuthRecentLoginRequiredException -> {
                                Toast.makeText(this, "Re-auth your account.", Toast.LENGTH_SHORT).show()
                                firebaseAuth.signOut()
                                val intent = Intent(this, SignUpActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Toast.makeText(this, "Error deleting account.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }

    }
}