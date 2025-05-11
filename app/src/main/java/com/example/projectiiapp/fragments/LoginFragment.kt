package com.example.projectiiapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectiiapp.AuthViewModel
import com.example.projectiiapp.R


class LoginFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        view.findViewById<Button>(R.id.btnLogin).setOnClickListener{
            val email = view.findViewById<EditText>(R.id.edtEmailLogin).text.toString()
            val password = view.findViewById<EditText>(R.id.edtPasswordLogin).text.toString()
            authViewModel.login(email, password)
        }

        view.findViewById<TextView>(R.id.txtLinkToSignUp).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return view
    }
}