package com.example.newshubapp.data.api

import com.example.newshubapp.data.models.NewsResponse
import com.example.newshubapp.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * News API Service - GNews API
 * Reference: https://gnews.io/docs/v4
 */
interface NewsApiService {

    @GET("api/v4/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "za",
        @Query("lang") lang: String = "en",
        @Query("category") category: String? = null,
        @Query("apikey") apiKey: String = Constants.NEWS_API_KEY
    ): NewsResponse

    @GET("api/v4/search")
    suspend fun searchArticles(
        @Query("q") query: String,
        @Query("lang") lang: String = "en",
        @Query("apikey") apiKey: String = Constants.NEWS_API_KEY,
        @Query("max") max: Int = 20
    ): NewsResponse
}