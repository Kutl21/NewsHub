package com.example.newshubapp.data.api

import com.example.newshubapp.utils.Constants  // ← ADD THIS IMPORT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * AP.\gradlew :app:kspDebugKotlin --debug 2>&1 | Out-File -FilePath "ksp-debug.txt"I Client Singleton
 * Creates and configures Retrofit instances for all API calls
 */
object ApiClient {

    // Base URLs for APIs
    private const val NEWS_BASE_URL = "https://gnews.io/"
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"

    /**
     * Logging interceptor to see network requests in Logcat
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * OkHttpClient with timeout configurations
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * News API Retrofit instance
     */
    val newsApi: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NEWS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    /**
     * Weather API Retrofit instance
     */
    val weatherApi: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}