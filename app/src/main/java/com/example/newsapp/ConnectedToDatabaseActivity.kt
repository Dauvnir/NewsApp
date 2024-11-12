package com.example.newsapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivitySuccessBinding

class ConnectedToDatabaseActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val apiKey = BuildConfig.API_KEY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        val languagePref = sharedPreferences.getString("LANGUAGES", null)?.replace(" ", "")
        val countryPref = sharedPreferences.getString("COUNTRY", null)?.replace(" ", "")
        val categoryPref = sharedPreferences.getString("CATEGORY", null)?.replace(" ", "")
        val baseUrl = "http://api.mediastack.com/v1/news"

        if(languagePref != null && countryPref != null && categoryPref !== null){
            val key = "?access_key=$apiKey"
            val country = "&countries=$countryPref"
            val language = "&languages=$languagePref"
            val category = "&category=$categoryPref"

            val finalUrl = "$baseUrl$key$country$language$category"
        }


    }
}