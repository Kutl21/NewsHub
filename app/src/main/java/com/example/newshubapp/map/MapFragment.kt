package com.example.newshubapp.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.newshubapp.databinding.FragmentMapBinding
import com.example.newshubapp.utils.Constants

/**
 * MapFragment - Displays Free State map with locations
 * Provides directions via Google Maps (app or browser fallback)
 */
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val mapViewModel: MapViewModel by viewModels()

    companion object {
        private const val TAG = "MapFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupSpinner()
        loadLocations()
        setupListeners()
    }

    /**
     * Setup UI elements
     */
    private fun setupUI() {
        Log.d(TAG, "MapFragment UI setup complete")
    }

    /**
     * Setup the location Spinner with Free State cities
     * Uses a real Spinner with a working dropdown
     */
    private fun setupSpinner() {
        val locations = Constants.FREE_STATE_LOCATIONS.map { it.name }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            locations
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLocation.adapter = adapter

        // Selection listener
        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = locations[position]
                Log.d(TAG, "Location selected: $selected")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "Nothing selected")
            }
        }

        Log.d(TAG, "Spinner setup with ${locations.size} locations")
    }

    /**
     * Show the number of Free State locations available
     */
    private fun loadLocations() {
        val locations = Constants.FREE_STATE_LOCATIONS
        binding.tvLocationCount.text = "${locations.size} Free State locations available"
        Log.d(TAG, "Loaded ${locations.size} Free State locations")
    }

    /**
     * Setup click listeners
     */
    private fun setupListeners() {
        binding.btnGetDirections.setOnClickListener {
            val selectedLocation = binding.spinnerLocation.selectedItem?.toString()
            if (!selectedLocation.isNullOrEmpty()) {
                Log.d(TAG, "Getting directions to: $selectedLocation")
                showDirections(selectedLocation)
            } else {
                Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRefresh.setOnClickListener {
            Toast.makeText(requireContext(), "Refreshing map...", Toast.LENGTH_SHORT).show()
            loadLocations()
        }
    }

    /**
     * Open Google Maps with directions to the selected location
     * 1. Tries Google Maps app (turn-by-turn navigation)
     * 2. Falls back to Google Maps in browser
     * 3. Falls back to any maps app
     */
    private fun showDirections(location: String) {
        val locationData = Constants.FREE_STATE_LOCATIONS.find { it.name == location }

        if (locationData == null) {
            Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Getting directions to: $location (${locationData.latitude}, ${locationData.longitude})")

        // ---- Attempt 1: Google Maps app (navigation) ----
        val navUri = Uri.parse(
            "google.navigation:q=${locationData.latitude},${locationData.longitude}"
        )

        try {
            val navIntent = Intent(Intent.ACTION_VIEW, navUri)
            navIntent.setPackage("com.google.android.apps.maps")
            if (navIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(navIntent)
                Log.d(TAG, "Opened Google Maps navigation app")
                return
            }
        } catch (e: Exception) {
            Log.e(TAG, "Nav intent failed: ${e.message}")
        }

        // ---- Attempt 2: Google Maps in browser ----
        val webUri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1&destination=${locationData.latitude},${locationData.longitude}"
        )

        try {
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(webIntent)
            Log.d(TAG, "Opened Google Maps in browser")
            return
        } catch (e: Exception) {
            Log.e(TAG, "Web intent failed: ${e.message}")
        }

        // ---- Attempt 3: Any maps app ----
        try {
            val genericUri = Uri.parse(
                "geo:${locationData.latitude},${locationData.longitude}?q=${locationData.latitude},${locationData.longitude}(${locationData.name})"
            )
            val genericIntent = Intent(Intent.ACTION_VIEW, genericUri)
            startActivity(genericIntent)
            Log.d(TAG, "Opened generic geo: app")
            return
        } catch (e: Exception) {
            Log.e(TAG, "Generic intent failed: ${e.message}")
        }

        // ---- All attempts failed ----
        Toast.makeText(
            requireContext(),
            "No maps app found. Install Google Maps to get directions.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}