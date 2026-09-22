package com.example.newshubapp.data.api

import com.example.newshubapp.data.models.WeatherResponse
import com.example.newshubapp.utils.Constants  // ← ADD THIS IMPORT
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Weather API Service Interface
 * Handles all network calls to Open-Meteo API
 * Reference: https://open-meteo.com/en/docs
 */
interface WeatherApiService {

    /**
     * Get current weather for a location
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @param current Weather parameters to fetch
     * @param timezone Timezone for the weather data
     * @return WeatherResponse containing current weather
     */
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weather_code,wind_speed_10m",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}