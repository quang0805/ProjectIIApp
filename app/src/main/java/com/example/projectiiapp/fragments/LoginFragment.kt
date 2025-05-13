package com.example.projectiiapp.fragments

import android.R.attr.text
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectiiapp.auth.AuthState
import com.example.projectiiapp.auth.AuthViewModel
import com.example.projectiiapp.R
import com.example.projectiiapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseUser
import com.hivemq.client.internal.util.Checks.state


class LoginFragment : Fragment() {



    private val authViewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        authViewModel.resetAuthState()

        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }


    private fun setupObservers() {
        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString().trim()
            val password = binding.edtPasswordLogin.text.toString().trim()

            if (validateInput(email, password)) {
                authViewModel.login(email, password)
            }
        }

        binding.txtLinkToSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }
    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.edtEmailLogin.error = "Email cannot be empty"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmailLogin.error = "Please enter a valid email"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.edtPasswordLogin.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.edtPasswordLogin.error = "Password must be at least 6 characters"
            isValid = false
        }
        return isValid
    }
    private fun updateUI(state: AuthState) {
        when (state) {
            is AuthState.Idle -> showIdleState()
            is AuthState.Loading -> showLoadingState()
            is AuthState.Success -> handleSuccess(state.userId)
            is AuthState.Error -> showErrorState(state.message)
            is AuthState.LoggedOut -> showLoggedOutState()
        }
    }
    private fun showIdleState() {
        with(binding) {
            progressBar.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            btnLogin.isEnabled = true
            txtError.visibility = View.GONE
            edtEmailLogin.isEnabled = true
            edtPasswordLogin.isEnabled = true
        }
    }

    private fun showLoadingState() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            btnLogin.visibility = View.GONE
            txtError.visibility = View.GONE
            edtEmailLogin.isEnabled = false
            edtPasswordLogin.isEnabled = false
        }
    }


    private fun handleSuccess(userId: String) {
        binding.btnLogin.visibility = View.VISIBLE
        binding.txtError.visibility = View.GONE

//        // Navigate to home screen
//        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun showErrorState(message: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            btnLogin.isEnabled = true
            txtError.text = message
            txtError.visibility = View.VISIBLE
            edtEmailLogin.isEnabled = true
            edtPasswordLogin.isEnabled = true

            // Add shake animation for visual feedback
            val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            root.startAnimation(shake)
        }
    }
    private fun showLoggedOutState() {
        with(binding) {
            edtEmailLogin.text?.clear()
            edtPasswordLogin.text?.clear()
            edtEmailLogin.error = null
            edtPasswordLogin.error = null
            txtError.visibility = View.GONE
            progressBar.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            btnLogin.isEnabled = true
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}