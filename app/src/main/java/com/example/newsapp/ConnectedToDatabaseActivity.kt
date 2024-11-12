package com.example.newsapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivitySuccessBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectedToDatabaseActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        val languagePref = sharedPreferences.getString("LANGUAGES", null)?.replace(" ", "") ?: ""
        val countryPref = sharedPreferences.getString("COUNTRY", null)?.replace(" ", "") ?: ""
        val categoryPref = sharedPreferences.getString("CATEGORY", null)?.replace(" ", "") ?: ""

        //fetchNews(languagePref, countryPref, categoryPref)
    }

    private fun fetchNews(categoryPref: String, languagePref: String, countryPref: String){
        val apiKey = BuildConfig.API_KEY
        val call = RetrofitInstance.api.getNews(
            accessKey = apiKey,
            categories = categoryPref,
            countries = countryPref,
            languages = languagePref,
        )

        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsList = response.body()?.data
                    // Handle the successful response (e.g., update UI)
                } else {
                    // Handle API error
                    // sent them to error page.
                    println("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                println("Failure: ${t.message}")
            }
        })

    }
}