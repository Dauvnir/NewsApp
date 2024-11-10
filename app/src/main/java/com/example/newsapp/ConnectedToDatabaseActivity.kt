package com.example.newsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivitySuccessBinding

class ConnectedToDatabaseActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding
    val apiKey = BuildConfig.API_KEY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}