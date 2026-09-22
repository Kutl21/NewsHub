package com.example.newshubapp.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newshubapp.utils.PreferencesManager

/**
 * SettingsViewModel - Handles settings logic
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val TAG = "SettingsViewModel"

    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    private val _areNotificationsEnabled = MutableLiveData<Boolean>()
    val areNotificationsEnabled: LiveData<Boolean> = _areNotificationsEnabled

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    init {
        loadSettings()
        Log.d(TAG, "SettingsViewModel initialized")
    }

    /**
     * Load all settings from preferences
     */
    private fun loadSettings() {
        _language.value = preferencesManager.getPreferredLanguage()
        _isDarkMode.value = preferencesManager.isDarkModeEnabled()
        _areNotificationsEnabled.value = preferencesManager.isNotificationEnabled()
        _userName.value = preferencesManager.getUserName() ?: "User"
        _userEmail.value = preferencesManager.getUserEmail() ?: "No email"
    }

    /**
     * Update language
     */
    fun updateLanguage(language: String) {
        preferencesManager.savePreferredLanguage(language)
        _language.value = language
        Log.d(TAG, "Language updated: $language")
    }

    /**
     * Update dark mode
     */
    fun updateDarkMode(enabled: Boolean) {
        preferencesManager.saveDarkModeEnabled(enabled)
        _isDarkMode.value = enabled
        Log.d(TAG, "Dark mode updated: $enabled")
    }

    /**
     * Update notifications
     */
    fun updateNotifications(enabled: Boolean) {
        preferencesManager.saveNotificationEnabled(enabled)
        _areNotificationsEnabled.value = enabled
        Log.d(TAG, "Notifications updated: $enabled")
    }

    /**
     * Clear all preferences
     */
    fun clearAllPreferences() {
        preferencesManager.clearAll()
        Log.d(TAG, "All preferences cleared")
    }
}