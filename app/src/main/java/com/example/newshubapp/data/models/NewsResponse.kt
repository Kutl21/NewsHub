package com.example.newshubapp.data.models

import com.google.gson.annotations.SerializedName

/**
 * News Response Model - GNews API
 * Wraps the API response from GNews
 * Reference: https://gnews.io/docs/v4
 *
 * GNews response format:
 * {
 *   "totalArticles": 35910,
 *   "articles": [ ... ]
 * }
 *
 * Note: GNews does NOT have a "status" field like NewsAPI does.
 */
data class NewsResponse(
    @SerializedName("totalArticles")
    val totalArticles: Int = 0,

    @SerializedName("articles")
    val articles: List<Article> = emptyList()
)