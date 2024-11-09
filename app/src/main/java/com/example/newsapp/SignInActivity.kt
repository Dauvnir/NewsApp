package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignInActivity: AppCompatActivity() {

    private lateinit var binding: ActivityPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun validateData(
        context: Context,
        email:String,
        pass:String,
        confirmPass:String,
        onComplete: (isValid: Boolean, errorMessage: String) -> Unit){

        val passwordPattern: Regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{8,20}$".toRegex()
        val emailPattern: Regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        if (!isInternetAvailable(context)) {
            onComplete(false, "No internet connection. Please check your network settings.")
            return
        }

        if(email.isEmpty() ||  pass.isEmpty() || confirmPass.isEmpty()) {
            onComplete(false, "All fields must be filled")
            return
        }

        if(pass != confirmPass){
            onComplete(false, "Passwords do not match.")
            return
        }

        if(!email.matches(emailPattern)) {
            onComplete(false, "Invalid email format.")
            return
        }

        if(!pass.matches(passwordPattern)){
            onComplete(false, "Password needs uppercase, lowercase, number, and special character.")
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
                if (it.isSuccessful){
                    onComplete(true, "")
                }else{
                    val exception = it.exception
                    if(exception is FirebaseAuthUserCollisionException){
                        onComplete(false, "Email is already in use. Please use a different email.")
                    }else{
                        onComplete(false, "Registration failed")
                    }
                }
            }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.loginBtn.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.registerBtn.setOnClickListener{
                val email = binding.editEmail.text.toString()
                val pass = binding.passwordInput.text.toString()
                val confirmPass = binding.rePasswordInput.text.toString()

                validateData(this, email, pass, confirmPass){
                    isValid, errorMessage -> if(isValid){
                       val intent = Intent(this, SignUpActivity::class.java)
                       startActivity(intent)
                    }else{
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}