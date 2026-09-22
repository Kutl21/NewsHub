package com.example.newshubapp.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.newshubapp.R
import com.example.newshubapp.databinding.FragmentWeatherBinding
import com.example.newshubapp.data.models.WeatherCodes
import com.google.android.gms.location.LocationServices

/**
 * WeatherFragment - Displays Free State weather
 */
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val weatherViewModel: WeatherViewModel by viewModels()

    companion object {
        private const val TAG = "WeatherFragment"
        private const val LOCATION_PERMISSION_REQUEST = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        checkLocationPermission()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvWeatherTitle.text = "🌤️ Free State Weather"
        Log.d(TAG, "WeatherFragment UI setup complete")
    }

    private fun setupObservers() {
        // Observe weather data
        weatherViewModel.weather.observe(viewLifecycleOwner) { weather ->
            if (weather != null) {
                displayWeather(weather)
                showWeatherData()
            }
        }

        // Observe loading state
        weatherViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error message
        weatherViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                showError(message)
            }
        }

        // Observe Free State status
        weatherViewModel.isInFreeState.observe(viewLifecycleOwner) { isInFreeState ->
            if (!isInFreeState) {
                showNotInFreeState()
            }
        }

        // Observe location name
        weatherViewModel.locationName.observe(viewLifecycleOwner) { location ->
            binding.tvLocationName.text = location ?: "Free State"
        }
    }

    private fun displayWeather(weather: com.example.newshubapp.data.models.WeatherResponse) {
        try {
            val current = weather.current
            val code = current.weatherCode

            binding.tvWeatherIcon.text = WeatherCodes.getWeatherIcon(code)
            binding.tvCondition.text = WeatherCodes.getWeatherDescription(code)
            binding.tvTemperature.text = "${current.temperature.toInt()}°C"
            binding.tvWindSpeed.text = "Wind: ${current.windSpeed.toInt()} km/h"

            // Update gradient
            updateWeatherGradient(code)

            Log.d(TAG, "Weather displayed: ${current.temperature}°C")
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying weather: ${e.message}")
        }
    }

    private fun updateWeatherGradient(weatherCode: Int) {
        val gradient = when (weatherCode) {
            0, 1 -> R.drawable.weather_gradient_sunny
            2, 3 -> R.drawable.weather_gradient_cloudy
            45, 48 -> R.drawable.weather_gradient_foggy
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> R.drawable.weather_gradient_rainy
            71, 73, 75 -> R.drawable.weather_gradient_snowy
            95, 96, 99 -> R.drawable.weather_gradient_stormy
            else -> R.drawable.weather_gradient
        }
        binding.cardWeather.setBackgroundResource(gradient)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocationAndWeather()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    private fun getLocationAndWeather() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    Log.d(TAG, "Location: ${location.latitude}, ${location.longitude}")
                    weatherViewModel.getWeather(location.latitude, location.longitude)
                } else {
                    Log.d(TAG, "Location is null, using default")
                    weatherViewModel.getWeather(-29.1211, 26.2140)
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to get location: ${e.message}")
                weatherViewModel.getWeather(-29.1211, 26.2140)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted")
                getLocationAndWeather()
            } else {
                Log.d(TAG, "Location permission denied")
                weatherViewModel.getWeather(-29.1211, 26.2140)
            }
        }
    }

    private fun setupListeners() {
        binding.btnRefresh.setOnClickListener {
            checkLocationPermission()
            Toast.makeText(requireContext(), "Refreshing weather...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showWeatherData() {
        binding.weatherContainer.visibility = View.VISIBLE
        binding.errorContainer.visibility = View.GONE
        binding.notInFreeStateContainer.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.weatherContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.VISIBLE
        binding.notInFreeStateContainer.visibility = View.GONE
        binding.tvErrorMessage.text = message
    }

    private fun showNotInFreeState() {
        binding.weatherContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        binding.notInFreeStateContainer.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}