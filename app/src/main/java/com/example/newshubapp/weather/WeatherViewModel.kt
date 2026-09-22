package com.example.newshubapp.weather

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newshubapp.data.models.WeatherResponse
import com.example.newshubapp.data.repository.NewsRepository
import com.example.newshubapp.utils.Constants
import kotlinx.coroutines.launch

/**
 * WeatherViewModel - Handles weather data and operations
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository(application)
    private val TAG = "WeatherViewModel"

    private val _weather = MutableLiveData<WeatherResponse?>()
    val weather: LiveData<WeatherResponse?> = _weather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isInFreeState = MutableLiveData<Boolean>()
    val isInFreeState: LiveData<Boolean> = _isInFreeState

    private val _locationName = MutableLiveData<String?>()
    val locationName: LiveData<String?> = _locationName

    init {
        Log.d(TAG, "WeatherViewModel initialized")
    }

    /**
     * Get weather for a location
     */
    fun getWeather(latitude: Double, longitude: Double) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(TAG, "Getting weather for: $latitude, $longitude")

                if (repository.isInFreeState(latitude, longitude)) {
                    Log.d(TAG, "Location is in Free State")
                    _isInFreeState.value = true

                    val weatherData = repository.getWeather(latitude, longitude)
                    if (weatherData != null) {
                        _weather.value = weatherData
                        _locationName.value = getLocationName(latitude, longitude)
                        Log.d(TAG, "Weather loaded: ${weatherData.current.temperature}°C")
                    } else {
                        _errorMessage.value = "Unable to fetch weather data"
                        Log.w(TAG, "Weather data is null")
                    }
                } else {
                    Log.d(TAG, "Location is outside Free State")
                    _isInFreeState.value = false
                    _errorMessage.value = "Weather services available for Free State only"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting weather: ${e.message}")
                _errorMessage.value = "Failed to get weather: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get location name from coordinates
     */
    private fun getLocationName(latitude: Double, longitude: Double): String? {
        return Constants.FREE_STATE_LOCATIONS.firstOrNull {
            Math.abs(it.latitude - latitude) < 0.01 &&
                    Math.abs(it.longitude - longitude) < 0.01
        }?.name ?: "Free State"
    }

    /**
     * Clear errors
     */
    fun clearErrors() {
        _errorMessage.value = null
    }
}