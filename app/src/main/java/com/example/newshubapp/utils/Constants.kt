package com.example.newshubapp.utils

/**
 * Application-wide constants
 * Contains API keys, category lists, preference keys, etc.
 */
object Constants {

    // ========== NEWS API ==========
    // Get your API key from https://newsapi.org
    const val NEWS_API_KEY = "5ebc084a7e8ebef1a0f486a7c4245b14"  // ← REPLACE WITH YOUR KEY

    // ========== FREE STATE LOCATIONS ==========
    val FREE_STATE_LOCATIONS = listOf(
        FreeStateLocation("Bloemfontein", -29.1211, 26.2140),
        FreeStateLocation("Welkom", -27.9774, 26.7358),
        FreeStateLocation("Bethlehem", -28.2308, 28.3071),
        FreeStateLocation("Kroonstad", -27.6500, 27.2333),
        FreeStateLocation("Sasolburg", -26.8136, 27.8270)
    )

    // Free State province bounding box
    const val FREE_STATE_MIN_LAT = -29.5
    const val FREE_STATE_MAX_LAT = -26.5
    const val FREE_STATE_MIN_LON = 24.5
    const val FREE_STATE_MAX_LON = 29.5

    // ========== SHARED PREFERENCES ==========
    const val PREF_NAME = "newshub_prefs"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_THEME = "theme"

    // ========== NEWS CATEGORIES ==========
    val CATEGORIES = listOf(
        "General",
        "Technology",
        "Business",
        "Sports",
        "Science",
        "Entertainment",
        "Health"
    )

    fun getCategoryApiParam(category: String): String? {
        return when (category) {
            "General" -> null
            "Technology" -> "technology"
            "Business" -> "business"
            "Sports" -> "sports"
            "Science" -> "science"
            "Entertainment" -> "entertainment"
            "Health" -> "health"
            else -> null
        }
    }
}

/**
 * Data class for Free State location
 */
data class FreeStateLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)