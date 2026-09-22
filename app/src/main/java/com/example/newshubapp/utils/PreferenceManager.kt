package com.example.newshubapp.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages SharedPreferences for storing user data
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME,
        Context.MODE_PRIVATE
    )

    // ========== AUTHENTICATION ==========

    fun saveUserLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(Constants.KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }

    fun saveUserId(userId: String) {
        prefs.edit().putString(Constants.KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(Constants.KEY_USER_ID, null)
    }

    fun saveUserName(userName: String) {
        prefs.edit().putString(Constants.KEY_USER_NAME, userName).apply()
    }

    fun getUserName(): String? {
        return prefs.getString(Constants.KEY_USER_NAME, null)
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString(Constants.KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(Constants.KEY_USER_EMAIL, null)
    }

    // ========== APP SETTINGS ==========

    fun saveDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    fun saveNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications", enabled).apply()
    }

    fun isNotificationEnabled(): Boolean {
        return prefs.getBoolean("notifications", true)
    }

    fun savePreferredLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
    }

    fun getPreferredLanguage(): String {
        return prefs.getString("language", "English") ?: "English"
    }

    // ========== CATEGORY PREFERENCES (NEW METHODS) ==========

    /**
     * Save selected categories as a comma-separated string
     */
    fun saveSelectedCategories(categories: List<String>) {
        val categoriesString = categories.joinToString(",")
        prefs.edit().putString("selected_categories", categoriesString).apply()
    }

    /**
     * Get selected categories from SharedPreferences
     */
    fun getSelectedCategories(): List<String>? {
        val categoriesString = prefs.getString("selected_categories", null)
        return categoriesString?.split(",")?.filter { it.isNotEmpty() }
    }

    /**
     * Get selected categories with default fallback
     */
    fun getSelectedCategoriesOrDefault(): List<String> {
        return getSelectedCategories() ?: listOf("General")
    }

    // ========== CLEAR DATA ==========

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}