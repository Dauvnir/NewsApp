package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity: AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun validateEmail(
        context: Context,
        email:String,
        onComplete: (errorMessage: String) -> Unit){

        val emailPattern: Regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        if (!isInternetAvailable(context)) {
            onComplete( "No internet connection. Please check your network settings.")
            return
        }

        if(email.isEmpty()) {
            onComplete( "Field must be filled")
            return
        }

        if(!email.matches(emailPattern)) {
            onComplete( "Invalid email format.")
            return
        }

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{
            if(it.isSuccessful){
                onComplete( "Password reset email sent. Check your inbox.")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.sendLink.setOnClickListener{
            val email = binding.emailAddress.text.toString()

            validateEmail(this, email){
                 errorMessage -> Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        binding.button2.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}