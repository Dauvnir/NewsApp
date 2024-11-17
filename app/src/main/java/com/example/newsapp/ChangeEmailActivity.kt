package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityChangeEmailBinding

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
    }
}