package com.example.newshubapp.data.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: CurrentWeather,
    @SerializedName("current_units")
    val units: WeatherUnits
)

data class CurrentWeather(
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("weather_code")
    val weatherCode: Int,
    @SerializedName("wind_speed_10m")
    val windSpeed: Double,
    val time: String
)

data class WeatherUnits(
    @SerializedName("temperature_2m")
    val temperatureUnit: String,
    @SerializedName("wind_speed_10m")
    val windSpeedUnit: String
)

object WeatherCodes {
    private val weatherMap = mapOf(
        0 to "Clear sky",
        1 to "Mainly clear",
        2 to "Partly cloudy",
        3 to "Overcast",
        45 to "Fog",
        48 to "Depositing rime fog",
        51 to "Light drizzle",
        53 to "Moderate drizzle",
        55 to "Dense drizzle",
        61 to "Slight rain",
        63 to "Moderate rain",
        65 to "Heavy rain",
        71 to "Slight snow fall",
        73 to "Moderate snow fall",
        75 to "Heavy snow fall",
        80 to "Slight rain showers",
        81 to "Moderate rain showers",
        82 to "Violent rain showers",
        95 to "Thunderstorm",
        96 to "Thunderstorm with slight hail",
        99 to "Thunderstorm with heavy hail"
    )

    fun getWeatherDescription(code: Int): String {
        return weatherMap[code] ?: "Unknown"
    }

    fun getWeatherIcon(code: Int): String {
        return when (code) {
            0, 1 -> "☀️"
            2 -> "⛅"
            3 -> "☁️"
            45, 48 -> "🌫️"
            51, 53, 55 -> "🌧️"
            61, 63, 65 -> "🌧️"
            71, 73, 75 -> "🌨️"
            80, 81, 82 -> "🌧️"
            95, 96, 99 -> "⛈️"
            else -> "🌤️"
        }
    }
}