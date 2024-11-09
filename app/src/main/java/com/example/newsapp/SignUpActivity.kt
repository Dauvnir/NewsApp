package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
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
        onComplete: (isValid: Boolean, errorMessage: String) -> Unit){

        val passwordPattern: Regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{8,20}$".toRegex()
        val emailPattern: Regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        if (!isInternetAvailable(context)) {
            onComplete(false, "No internet connection. Please check your network settings.")
            return
        }

        if(email.isEmpty() ||  pass.isEmpty()) {
            onComplete(false, "All fields must be filled")
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

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener{
            if(it.isSuccessful){
                onComplete(true, "")

                if(binding.checkBox.isChecked){
                    sharedPreferences.edit().putBoolean("AUTO_LOGIN", true).apply()
                }
            }else{
                onComplete(false, "Email or password is wrong. Try again.")

            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.createAccountBtn.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.forgotPassword.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        binding.loginBtn.setOnClickListener{
            val email = binding.emailInput.text.toString()
            val pass = binding.passwordInput.text.toString()

            validateData(this, email, pass){ isValid, errorMessage ->
                if(isValid){
                    if (binding.checkBox.isChecked) {
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("AUTO_LOGIN", true)
                        editor.apply()
                    }

                    val intent = Intent(this, ConnectedToDatabaseActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val autoLoginEnabled = sharedPreferences.getBoolean("AUTO_LOGIN", false)
        if(firebaseAuth.currentUser != null && autoLoginEnabled){
            val intent = Intent(this, ConnectedToDatabaseActivity::class.java)
            startActivity(intent)
        }
    }

}