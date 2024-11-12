package com.example.newsapp

data class NewsResponse(
    val data: List<NewsArticle>,
    val pagination: Pagination
)

data class NewsArticle(
    val author: String?,
    val title: String,
    val description: String,
    val url: String,
    val source: String,
    val image: String?,
    val published_at: String
)

data class Pagination(
    val limit: Int,
    val offset: Int,
    val total: Int,
    val count: Int
)