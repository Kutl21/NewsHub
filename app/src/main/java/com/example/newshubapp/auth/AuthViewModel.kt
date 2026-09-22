package com.example.newshubapp.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AuthViewModel handles authentication with Firebase
 * Uses Complete Listeners for all async tasks
 */
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    companion object {
        private const val TAG = "AuthViewModel"
    }

    init {
        auth.currentUser?.let { user ->
            _authState.value = AuthState.Authenticated(user)
            Log.d(TAG, "User already signed in: ${user.email}")
        }
    }

    fun isAuthenticated(): Boolean {
        val isLoggedIn = auth.currentUser != null
        Log.d(TAG, "isAuthenticated: $isLoggedIn")
        return isLoggedIn
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // =====================================================
    // REGISTER — with Complete Listener
    // =====================================================

    fun register(email: String, password: String, username: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            _errorMessage.value = "All fields are required"
            return
        }

        _isLoading.value = true
        Log.d(TAG, "═══ REGISTER START: $email ═══")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false

                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Register task succeeded")
                    val user = auth.currentUser
                    user?.let {
                        saveUserToFirestore(it.uid, username, email)
                        _authState.value = AuthState.Authenticated(it)
                        _successMessage.value = "Registration successful!"
                        Log.d(TAG, "Register complete: ${it.email} (uid=${it.uid})")
                    }
                } else {
                    val error = getErrorMessage(task.exception)
                    _errorMessage.value = error
                    Log.e(TAG, "❌ Register failed: $error", task.exception)
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                val error = getErrorMessage(e)
                _errorMessage.value = error
                Log.e(TAG, "❌ Register failure listener: $error")
            }
    }

    // =====================================================
    // LOGIN — with Complete Listener
    // =====================================================

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Email and password are required"
            return
        }

        _isLoading.value = true
        Log.d(TAG, "═══ LOGIN START: $email ═══")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false

                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Login task succeeded")
                    val user = auth.currentUser
                    user?.let {
                        _authState.value = AuthState.Authenticated(it)
                        _successMessage.value = "Welcome back!"
                        Log.d(TAG, "Login complete: ${it.email}")
                    }
                } else {
                    val error = getErrorMessage(task.exception)
                    _errorMessage.value = error
                    Log.e(TAG, "❌ Login failed: $error", task.exception)
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                val error = getErrorMessage(e)
                _errorMessage.value = error
                Log.e(TAG, "❌ Login failure listener: $error")
            }
    }

    // =====================================================
    // GOOGLE SIGN-IN — with Complete Listener
    // =====================================================

    fun signInWithGoogle(idToken: String) {
        _isLoading.value = true
        Log.d(TAG, "═══ GOOGLE SIGN-IN START ═══")

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false

                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Google task succeeded")
                    val user = auth.currentUser
                    user?.let {
                        checkAndSaveGoogleUser(it)
                        _authState.value = AuthState.Authenticated(it)
                        _successMessage.value = "Google Sign-In successful!"
                        Log.d(TAG, "Google complete: ${it.email}")
                    }
                } else {
                    val error = getErrorMessage(task.exception)
                    _errorMessage.value = error
                    Log.e(TAG, "❌ Google failed: $error", task.exception)
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                val error = getErrorMessage(e)
                _errorMessage.value = error
                Log.e(TAG, "❌ Google failure listener: $error")
            }
    }

    // =====================================================
    // FIRESTORE — save user
    // =====================================================

    private fun saveUserToFirestore(uid: String, username: String, email: String) {
        Log.d(TAG, "Saving user to Firestore: $uid")

        val userData = hashMapOf(
            "username" to username,
            "email" to email,
            "createdAt" to System.currentTimeMillis(),
            "provider" to "email"
        )

        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Firestore save succeeded")
                } else {
                    Log.e(TAG, "❌ Firestore save failed", task.exception)
                }
            }
    }

    private fun checkAndSaveGoogleUser(user: FirebaseUser) {
        Log.d(TAG, "Checking Google user: ${user.uid}")

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && !document.exists()) {
                        val userData = hashMapOf(
                            "username" to (user.displayName ?: "Google User"),
                            "email" to user.email,
                            "createdAt" to System.currentTimeMillis(),
                            "photoUrl" to user.photoUrl?.toString(),
                            "provider" to "google"
                        )

                        firestore.collection("users")
                            .document(user.uid)
                            .set(userData)
                            .addOnCompleteListener { saveTask ->
                                if (saveTask.isSuccessful) {
                                    Log.d(TAG, "✅ Google user saved to Firestore")
                                } else {
                                    Log.e(TAG, "❌ Google user save failed", saveTask.exception)
                                }
                            }
                    } else {
                        Log.d(TAG, "Google user already exists in Firestore")
                    }
                } else {
                    Log.e(TAG, "❌ Firestore check failed", task.exception)
                }
            }
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    fun logout() {
        Log.d(TAG, "═══ LOGOUT ═══")
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        Log.d(TAG, "✅ Logged out")
    }

    // =====================================================
    // PASSWORD RESET — with Complete Listener
    // =====================================================

    fun resetPassword(email: String) {
        if (email.isEmpty()) {
            _errorMessage.value = "Please enter your email address"
            return
        }

        _isLoading.value = true
        Log.d(TAG, "═══ PASSWORD RESET: $email ═══")

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _successMessage.value = "Password reset email sent!"
                    Log.d(TAG, "✅ Reset email sent to: $email")
                } else {
                    val error = getErrorMessage(task.exception)
                    _errorMessage.value = error
                    Log.e(TAG, "❌ Reset failed: $error")
                }
            }
    }

    // =====================================================
    // ERROR MESSAGE HELPER
    // =====================================================

    /**
     * Convert Firebase exceptions into friendly user messages
     */
    private fun getErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException ->
                "Password is too weak. Use at least 6 characters."

            is FirebaseAuthInvalidCredentialsException ->
                "Invalid email or password. Please try again."

            is FirebaseAuthInvalidUserException ->
                "No account found with this email."

            is FirebaseAuthUserCollisionException ->
                "An account already exists with this email."

            is FirebaseAuthException ->
                exception.message ?: "Authentication error occurred."

            else ->
                exception?.message ?: "Unknown error occurred."
        }
    }
}

/**
 * Authentication state sealed class
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
}