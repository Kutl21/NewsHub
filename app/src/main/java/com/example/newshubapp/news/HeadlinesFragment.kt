package com.example.newshubapp.news

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newshubapp.R
import com.example.newshubapp.data.models.WeatherCodes
import com.example.newshubapp.data.models.WeatherResponse
import com.example.newshubapp.databinding.FragmentHeadlinesBinding
import com.google.android.gms.location.LocationServices

/**
 * HeadlinesFragment - Main news feed
 * Displays news headlines with weather + search bar
 */
class HeadlinesFragment : Fragment() {

    private var _binding: FragmentHeadlinesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by activityViewModels()
    private lateinit var adapter: NewsAdapter

    companion object {
        private const val TAG = "HeadlinesFragment"
        private const val LOCATION_PERMISSION_REQUEST = 1001

        private const val DEFAULT_LATITUDE = -29.1211
        private const val DEFAULT_LONGITUDE = 26.2140
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHeadlinesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupSearch()
        setupObservers()
        checkLocationPermission()
    }

    private fun setupRecyclerView() {
        // FIXED: Removed onItemClick since the Adapter handles opening the URL directly now
        adapter = NewsAdapter(
            onBookmarkClick = { article -> viewModel.toggleBookmark(article) }
        )

        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HeadlinesFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.d(TAG, "Pull-to-refresh triggered")
            viewModel.refreshNews()
            checkLocationPermission()
        }
    }

    /**
     * Setup search bar listeners
     */
    private fun setupSearch() {
        // Search button click
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        // Search on keyboard "search" key
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }
    }

    /**
     * Execute search with current query
     */
    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "Type something to search", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Performing search: $query")
        hideKeyboard()
        viewModel.searchArticles(query)
    }

    /**
     * Hide soft keyboard
     */
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
        binding.etSearch.clearFocus()
    }

    private fun setupObservers() {
        // News
        viewModel.news.observe(viewLifecycleOwner) { articles ->
            if (articles.isNotEmpty()) {
                adapter.submitList(articles)
                showNewsList()
                Log.d(TAG, "News updated: ${articles.size} articles")
            } else {
                showEmptyState()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                showErrorState(message)
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // Weather
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            if (weather != null) displayWeather(weather)
        }

        viewModel.weatherLocation.observe(viewLifecycleOwner) { location ->
            binding.tvLocationName.text = location ?: "Free State"
        }

        viewModel.isInFreeState.observe(viewLifecycleOwner) { isInFreeState ->
            if (isInFreeState) {
                binding.cardWeather.visibility = View.VISIBLE
                binding.cardNoWeather.visibility = View.GONE
            } else {
                binding.cardWeather.visibility = View.GONE
                binding.cardNoWeather.visibility = View.VISIBLE
            }
        }

        viewModel.weatherErrorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty() && !message.contains("Free State")) {
                Log.d(TAG, "Weather error: $message")
            }
        }
    }

    private fun displayWeather(weather: WeatherResponse) {
        try {
            val current = weather.current
            val code = current.weatherCode
            val unit = weather.units

            binding.tvWeatherIcon.text = WeatherCodes.getWeatherIcon(code)
            binding.tvWeatherCondition.text = WeatherCodes.getWeatherDescription(code)
            binding.tvTemperature.text = "${current.temperature.toInt()}°${unit.temperatureUnit.first()}"
            binding.tvWindSpeed.text = "${current.windSpeed.toInt()} ${unit.windSpeedUnit}"

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

    // ==========================================================
    // LOCATION
    // ==========================================================

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
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "No location permission — using default Bloemfontein")
            viewModel.getWeather(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d(TAG, "Got location: ${location.latitude}, ${location.longitude}")
                    viewModel.getWeather(location.latitude, location.longitude)
                } else {
                    Log.d(TAG, "Location is null — using default Bloemfontein")
                    viewModel.getWeather(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get location: ${e.message}")
                viewModel.getWeather(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
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
                getLocationAndWeather()
            } else {
                viewModel.getWeather(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
            }
        }
    }

    // ==========================================================
    // UI HELPERS
    // ==========================================================

    private fun showNewsList() {
        binding.rvNews.visibility = View.VISIBLE
        binding.llEmptyState.visibility = View.GONE
        binding.llErrorState.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.rvNews.visibility = View.GONE
        binding.llEmptyState.visibility = View.VISIBLE
        binding.llErrorState.visibility = View.GONE
    }

    private fun showErrorState(message: String) {
        binding.rvNews.visibility = View.GONE
        binding.llEmptyState.visibility = View.GONE
        binding.llErrorState.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message

        binding.btnRetry.setOnClickListener {
            viewModel.refreshNews()
            checkLocationPermission()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}