package com.example.projectiiapp.fragments

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
import com.example.projectiiapp.AuthState
import com.example.projectiiapp.AuthViewModel
import com.example.projectiiapp.R
import com.example.projectiiapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseUser
import com.hivemq.client.internal.util.Checks.state


class SignUpFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers(){
        authViewModel.authState.observe(viewLifecycleOwner){state ->
            updateUI(state)
        }
    }
    private fun updateUI(state: AuthState) {
        when (state) {
            is AuthState.Idle -> showIdleState()
            is AuthState.Loading -> showLoadingState()
            is AuthState.Success -> handleSuccess(state.userId)
            is AuthState.Error -> showErrorState(state.message)
            else -> Unit
        }
    }

    private fun setupListeners(){
        binding.btnSignUp.setOnClickListener {
            val email = binding.edtEmailSignUp.text.toString().trim()
            val password = binding.edtPasswordSignUp.text.toString().trim()
            val confirmPassword = binding.edtConfirmPassword.text.toString().trim()
            if (validateInput(email, password, confirmPassword)) {
                authViewModel.signUp(email, password)
            }
        binding.txtQuestionAlready.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
        }
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Validate email
        if (email.isEmpty()) {
            binding.edtEmailSignUp.error = "Email cannot be empty"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmailSignUp.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.edtEmailSignUp.error = null
        }

        // Validate password
        if (password.isEmpty()) {
            binding.edtPasswordSignUp.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.edtPasswordSignUp.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.edtPasswordSignUp.error = null
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            binding.edtConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.edtConfirmPassword.error = "Passwords don't match"
            isValid = false
        } else {
            binding.edtConfirmPassword.error = null
        }

        return isValid
    }



    private fun showIdleState() {
        with(binding) {
            progressBarSignUp.visibility = View.GONE
            btnSignUp.visibility = View.VISIBLE
            btnSignUp.isEnabled = true
            txtError.visibility = View.GONE
            edtEmailSignUp.isEnabled = true
            edtPasswordSignUp.isEnabled = true
            edtConfirmPassword.isEnabled = true
        }
    }

    private fun showLoadingState() {
        with(binding) {
            progressBarSignUp.visibility = View.VISIBLE
            btnSignUp.visibility = View.GONE
            btnSignUp.isEnabled = false
            txtError.visibility = View.GONE
            edtEmailSignUp.isEnabled = false
            edtPasswordSignUp.isEnabled = false
            edtConfirmPassword.isEnabled = false
        }
    }

    private fun handleSuccess(userId: String) {
//        // Navigate to home screen
//        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
    }

    private fun showErrorState(message: String) {
        with(binding) {
            btnSignUp.visibility = View.VISIBLE
            progressBarSignUp.visibility = View.GONE
            btnSignUp.isEnabled = true
            txtError.text = message
            txtError.visibility = View.VISIBLE
            edtEmailSignUp.isEnabled = true
            edtPasswordSignUp.isEnabled = true
            edtConfirmPassword.isEnabled = true

            // Shake animation for error feedback
            val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            root.startAnimation(shake)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}