package com.example.newshubapp.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newshubapp.R
import com.example.newshubapp.auth.AuthViewModel
import com.example.newshubapp.databinding.FragmentSettingsBinding
import com.example.newshubapp.utils.PreferencesManager
import kotlinx.coroutines.launch

/**
 * SettingsFragment - User preferences and settings
 * Handles dark mode, notifications, language, and logout
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var preferencesManager: PreferencesManager

    companion object {
        private const val TAG = "SettingsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())

        setupUserInfo()
        setupLanguageSpinner()
        setupSwitches()
        setupCategoryPreferences()
        setupLogoutButton()
        observeAuthState()
    }

    private fun setupUserInfo() {
        val userName = preferencesManager.getUserName() ?: "User"
        val userEmail = preferencesManager.getUserEmail() ?: "No email"

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail

        Log.d(TAG, "User info displayed: $userName")
    }

    private fun setupLanguageSpinner() {
        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languages
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        val currentLanguage = preferencesManager.getPreferredLanguage()
        val position = languages.indexOf(currentLanguage)
        if (position != -1) {
            binding.spinnerLanguage.setSelection(position)
        }

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = languages[position]
                preferencesManager.savePreferredLanguage(selected)
                Log.d(TAG, "Language changed to: $selected")
                Toast.makeText(requireContext(), "Language: $selected", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSwitches() {
        // Dark Mode
        binding.switchDarkMode.isChecked = preferencesManager.isDarkModeEnabled()
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.saveDarkModeEnabled(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            Log.d(TAG, "Dark mode: $isChecked")
            Toast.makeText(
                requireContext(),
                if (isChecked) "Dark mode enabled" else "Light mode enabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Notifications
        binding.switchNotifications.isChecked = preferencesManager.isNotificationEnabled()
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.saveNotificationEnabled(isChecked)
            Log.d(TAG, "Notifications: $isChecked")
            Toast.makeText(
                requireContext(),
                if (isChecked) "Notifications enabled" else "Notifications disabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupCategoryPreferences() {
        try {
            val categories = resources.getStringArray(R.array.news_categories).toList()
            val savedCategories = preferencesManager.getSelectedCategories() ?: listOf("General")

            Log.d(TAG, "Categories: $categories")
            Log.d(TAG, "Saved Categories: $savedCategories")

            val adapter = CategoryPreferenceAdapter(
                allCategories = categories,
                selectedCategories = savedCategories,
                onSelectionChanged = { selectedCategories ->
                    preferencesManager.saveSelectedCategories(selectedCategories)
                    Log.d(TAG, "Categories updated: $selectedCategories")
                    Toast.makeText(requireContext(), "Categories updated", Toast.LENGTH_SHORT).show()
                }
            )

            binding.rvCategoryPreferences.apply {
                layoutManager = LinearLayoutManager(requireContext())
                this.adapter = adapter
            }

            Log.d(TAG, "Category preferences setup complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up category preferences: ${e.message}")
            Toast.makeText(requireContext(), "Error loading categories", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            Log.d(TAG, "Logout clicked")
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                authViewModel.logout()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is com.example.newshubapp.auth.AuthState.Unauthenticated -> {
                        Log.d(TAG, "User logged out")
                    }
                    is com.example.newshubapp.auth.AuthState.Authenticated -> {
                        Log.d(TAG, "User authenticated: ${state.user.email}")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Category Preference Adapter
 */
class CategoryPreferenceAdapter(
    private val allCategories: List<String>,
    private val selectedCategories: List<String>,
    private val onSelectionChanged: (List<String>) -> Unit
) : RecyclerView.Adapter<CategoryPreferenceAdapter.CategoryViewHolder>() {

    private val selectedItems = selectedCategories.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_preference, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = allCategories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = allCategories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbCategory)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: String) {
            tvCategoryName.text = category
            checkBox.isChecked = selectedItems.contains(category)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!selectedItems.contains(category)) {
                        selectedItems.add(category)
                    }
                } else {
                    selectedItems.remove(category)
                }
                onSelectionChanged(selectedItems.toList())
            }
        }
    }
}