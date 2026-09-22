package com.example.newshubapp.data.models

import java.io.Serializable

/**
 * User data model for Firebase Authentication
 * Stores user profile information in Firestore
 */
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val preferences: UserPreferences = UserPreferences()
) : Serializable

/**
 * User preferences stored in Firestore
 * Includes news categories, language, and notification settings
 */
data class UserPreferences(
    val selectedCategories: List<String> = listOf("General"),
    val preferredLanguage: String = "English",
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val followedTopics: List<String> = emptyList()
) : Serializable

/**
 * User login response
 */
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)

/**
 * User registration response
 */
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val uid: String? = null
)