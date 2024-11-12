package com.example.newsapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MediaStackApi {
    @GET("v1/news")
    fun getNews(
        @Query("access_key") accessKey: String,
        @Query("categories") categories: String,
        @Query("countries") countries: String,
        @Query("languages") languages: String,
        ): Call<NewsResponse>
}