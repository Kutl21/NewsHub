package com.example.newshubapp.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.newshubapp.R
import com.example.newshubapp.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

/**
 * Register Fragment for NewsHub
 * Handles new user registration.
 * After registration, redirects to log in screen (NOT directly to Home).
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    // Flag: did the user just register?
    // If true, we navigate to log in instead of Home when auth state changes
    private var justRegistered = false

    companion object {
        private const val TAG = "RegisterFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(username, email, password, confirmPassword)) {
                Log.d(TAG, "═══ REGISTER START: $email ═══")

                // Mark that we just registered
                justRegistered = true

                authViewModel.register(email, password, username)
            }
        }

        binding.tvLoginLink.setOnClickListener {
            Log.d(TAG, getString(R.string.navigate_to_login))
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    private fun validateInputs(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty()) {
            binding.etUsername.error = "Username is required"
            return false
        }
        if (username.length < 3) {
            binding.etUsername.error = "Username must be at least 3 characters"
            return false
        }
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Please confirm your password"
            return false
        }
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun observeAuthState() {
        // Observe authentication state
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        if (justRegistered) {
                            // ✅ User just registered — log out and go to log in screen
                            Log.d(TAG, "User registered: ${state.user.email}")
                            Log.d(TAG, "Logging out to force manual log in")

                            // Show success toast
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.registration_successful),
                                Toast.LENGTH_LONG
                            ).show()

                            // Sign out the auto-signed-in user
                            authViewModel.logout()

                            // Navigate to log in screen
                            findNavController().navigate(R.id.action_register_to_login)

                            // Reset flag
                            justRegistered = false
                        } else {
                            Log.d(TAG, "User already authenticated: ${state.user.email}")
                        }
                    }

                    is AuthState.Unauthenticated -> {
                        // Stay on register screen
                        Log.d(TAG, "User unauthenticated — staying on register screen")
                    }
                }
            }
        }

        // Observe error messages
        authViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                Log.d(TAG, "Error: $message")
            }
        }

        // Observe success messages
        authViewModel.successMessage.observe(viewLifecycleOwner) { message ->
            Log.d(TAG, "Success: $message")
        }

        // ✅ FIXED: Removed progressBar reference to fix compilation error
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnRegister.isEnabled = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

