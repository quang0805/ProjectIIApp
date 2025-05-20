package com.example.projectiiapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.projectiiapp.R
import com.example.projectiiapp.auth.AuthState
import com.example.projectiiapp.auth.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GreetingFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_greeting, container, false)

        lifecycleScope.launch {
            delay(3000) // 3 giây
            // 👇 Việc bạn muốn làm sau khi delay
            if(viewModel.authState.value == AuthState.Idle){
                findNavController().navigate(R.id.action_greetingFragment_to_loginFragment)
            }

        }

        return view
    }
}