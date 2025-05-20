package com.example.projectiiapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.projectiiapp.R
import com.example.projectiiapp.databinding.FragmentTemperatureBinding

class TemperatureFragment : Fragment() {
    private lateinit var binding: FragmentTemperatureBinding
    private val viewModel: HiveMqViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTemperatureBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.sensorData.observe(viewLifecycleOwner) { sensorData ->
            binding.txtTemperature.text = "${sensorData.temperature.toString()} °C"
        }
    }
}