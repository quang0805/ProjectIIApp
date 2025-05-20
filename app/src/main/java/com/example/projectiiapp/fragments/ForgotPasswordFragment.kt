package com.example.projectiiapp.fragments

import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.projectiiapp.R
import com.example.projectiiapp.databinding.FragmentForgotPasswordBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Khởi tạo Firebase Auth
        auth = Firebase.auth

        // Xử lý sự kiện khi nhấn nút gửi
        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailEditText.error = "Vui lòng nhập email"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = "Email không hợp lệ"
                return@setOnClickListener
            }

            sendPasswordResetEmail(email)
        }
    }

    fun sendPasswordResetEmail(email: String){
        // Hiển thị progress bar
        binding.progressBar.visibility = View.VISIBLE
        binding.resetPasswordButton.isEnabled = false

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Ẩn progress bar
                binding.progressBar.visibility = View.GONE
                binding.resetPasswordButton.isEnabled = true

                if (task.isSuccessful) {
                    binding.statusMessage.text = "Đã gửi liên kết đặt lại mật khẩu đến $email"
                    binding.statusMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_color))

                    Toast.makeText(
                        requireContext(),
                        "Kiểm tra email để đặt lại mật khẩu",
                        Toast.LENGTH_LONG
                    ).show()

                    lifecycleScope.launch {
                        delay(3000)
                        findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                    }
                } else {
                    binding.statusMessage.text = "Gửi email thất bại: ${task.exception?.message}"
                    binding.statusMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

                    Toast.makeText(
                        requireContext(),
                        "Lỗi: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}