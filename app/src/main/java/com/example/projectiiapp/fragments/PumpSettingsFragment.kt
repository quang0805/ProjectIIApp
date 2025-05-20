package com.example.projectiiapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.projectiiapp.R
import com.example.projectiiapp.databinding.FragmentPumpSettingsBinding
import org.json.JSONObject
//import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PumpSettingsFragment : Fragment() {
    private val viewModel: HiveMqViewModel by activityViewModels()
    private var _binding: FragmentPumpSettingsBinding? = null
    private val binding get() = _binding!!
    private var pumpStatus = "OFF"

    private var currentPumpSettings: PumpSettingsDialog.PumpSettings? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPumpSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDefaultSettings()
        setupUI()
    }

    private fun initDefaultSettings() {
        val calendar = Calendar.getInstance()
        currentPumpSettings = PumpSettingsDialog.PumpSettings(
            enabled = true,
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            day = calendar.get(Calendar.DAY_OF_MONTH),
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
            second = 0,
            duration = 5
        )
    }

    private fun setupUI() {
        updateUI()

        binding.btnSettings.setOnClickListener {
            currentPumpSettings?.let { settings ->
                showPumpSettingsDialog(settings)
            }
        }
    }

    private fun showPumpSettingsDialog(settings: PumpSettingsDialog.PumpSettings) {
        PumpSettingsDialog.show(requireContext(), settings) { newSettings ->
            currentPumpSettings = newSettings
            updateUI()
            publishPumpSchedule(newSettings)
        }
    }

    private fun updateUI() {
        currentPumpSettings?.let { settings ->
            binding.switchStatus.isChecked = settings.enabled
            binding.tvStatus.text = if (settings.enabled) "ĐANG BẬT" else "ĐANG TẮT"
            binding.tvStatus.setTextColor(
                if (settings.enabled) getColor(android.R.color.holo_green_dark)
                else getColor(android.R.color.holo_red_dark)
            )

            val calendar = Calendar.getInstance().apply {
                set(settings.year, settings.month - 1, settings.day,
                    settings.hour, settings.minute, settings.second)
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.tvSchedule.text = dateFormat.format(calendar.time)
            binding.tvDuration.text = "${settings.duration} phút"



            binding.btnPump.setOnClickListener {
                if(pumpStatus == "OFF"){
                    pumpStatus = "ON"
                    binding.btnPump.text = "TẮT BƠM"
                    binding.btnPump.setBackgroundColor(getColor(R.color.red))
                }else{
                    pumpStatus = "OFF"
                    binding.btnPump.text = "BƠM"
                    binding.btnPump.setBackgroundColor(getColor(R.color.primary_color))
                }
                viewModel.publishMessage("pump/control", pumpStatus)
            }
        }
    }

    private fun publishPumpSchedule(settings: PumpSettingsDialog.PumpSettings) {
        try {
            val json = JSONObject().apply {
                put("enabled", settings.enabled)
                put("year", settings.year)
                put("month", settings.month)
                put("day", settings.day)
                put("hour", settings.hour)
                put("minute", settings.minute)
                put("second", settings.second)
                put("duration", settings.duration)
            }.toString()

            viewModel.publishMessage("pump/schedule", json)
            showToast("Đã cập nhật thời gian bơm")
        } catch (e: Exception) {
            Log.e("PumpSettingsFragment", "Error publishing pump schedule: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getColor(colorRes: Int): Int {
        return requireContext().getColor(colorRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}