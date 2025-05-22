package com.walid.abahri.mealplanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.walid.abahri.mealplanner.ViewModel.LoginViewModel
import com.walid.abahri.mealplanner.R
import com.walid.abahri.mealplanner.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If already logged in (extra safety check, in case navGraph didn't already skip this)
        val prefs = requireActivity().getSharedPreferences("app_prefs", 0)
        if (prefs.getBoolean("isLoggedIn", false)) {
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
            return  // Skip login if user is already logged in
        }

        // Observe the login result LiveData from ViewModel
        loginViewModel.loginResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                // On successful login, save state in SharedPreferences
                prefs.edit().putBoolean("isLoggedIn", true)
                    .putString("username", binding.editTextUsername.text.toString())
                    .apply()


                // Navigate to Dashboard and clear back stack (action defined in nav_graph)
                findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
            } else {
                // Login failed: show a message
                Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Login button
        binding.buttonLogin.setOnClickListener {
            val user = binding.editTextUsername.text.toString().trim()
            val pass = binding.editTextPassword.text.toString().trim()
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter username and password", Toast.LENGTH_SHORT).show()
            } else {
                // Trigger login check via ViewModel
                loginViewModel.login(user, pass)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}