package com.example.newshubapp.map

import androidx.lifecycle.ViewModel
import android.util.Log
import com.example.newshubapp.utils.Constants

/**
 * MapViewModel - Handles map data and location logic
 */
class MapViewModel : ViewModel() {

    companion object {
        private const val TAG = "MapViewModel"
    }

    /**
     * Get all Free State locations
     */
    fun getLocations() = Constants.FREE_STATE_LOCATIONS

    /**
     * Get location by name
     */
    fun getLocationByName(name: String) =
        Constants.FREE_STATE_LOCATIONS.find { it.name == name }

    /**
     * Check if location is in Free State
     */
    fun isInFreeState(latitude: Double, longitude: Double): Boolean {
        return latitude in Constants.FREE_STATE_MIN_LAT..Constants.FREE_STATE_MAX_LAT &&
                longitude in Constants.FREE_STATE_MIN_LON..Constants.FREE_STATE_MAX_LON
    }

    /**
     * Get directions URI for Google Maps
     */
    fun getDirectionsUri(latitude: Double, longitude: Double): String {
        return "google.navigation:q=$latitude,$longitude"
    }
}