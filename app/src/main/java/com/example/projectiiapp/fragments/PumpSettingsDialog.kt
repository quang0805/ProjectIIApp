package com.example.projectiiapp.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.projectiiapp.R
import com.example.projectiiapp.databinding.FragmentPumpSettingsDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PumpSettingsDialog(
    private val context: Context,
    private val currentSettings: PumpSettings,
    private val onSave: (PumpSettings) -> Unit
) : Dialog(context, R.style.CustomDialogTheme) {

    private lateinit var binding: FragmentPumpSettingsDialogBinding

    data class PumpSettings(
        val enabled: Boolean,
        val year: Int,
        val month: Int,
        val day: Int,
        val hour: Int,
        val minute: Int,
        val second: Int,
        val duration: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPumpSettingsDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setupViews()
        setListeners()
    }

    private fun setupViews() {
        binding.switchEnabled.isChecked = currentSettings.enabled
        binding.editDuration.setText(currentSettings.duration.toString())

        val calendar = Calendar.getInstance().apply {
            set(currentSettings.year, currentSettings.month - 1, currentSettings.day,
                currentSettings.hour, currentSettings.minute, currentSettings.second)
        }
        updateDateTimeDisplay(calendar)
    }

    private fun setListeners() {
        binding.editDate.setOnClickListener { showDatePicker() }
        binding.editTime.setOnClickListener { showTimePicker() }

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveSettings()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            android.R.style.Theme_Material_Dialog,
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
                updateDateTimeDisplay(selectedCalendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            R.style.TimePickerTheme,
            { _, hour, minute ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                updateDateTimeDisplay(selectedCalendar)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }



    private fun updateDateTimeDisplay(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.editDate.setText(dateFormat.format(calendar.time))

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.editTime.setText(timeFormat.format(calendar.time))
    }

    private fun validateInput(): Boolean {
        if (binding.editDuration.text.toString().trim().isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập thời lượng bơm", Toast.LENGTH_SHORT).show()
            return false
        }

        val duration = binding.editDuration.text.toString().toInt()
        if (duration <= 0 || duration > 120) {
            Toast.makeText(context, "Thời lượng bơm phải từ 1-120 phút", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveSettings() {
        try {
            val dateTimeStr = "${binding.editDate.text} ${binding.editTime.text}"
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateTime = dateFormat.parse(dateTimeStr)

            val calendar = Calendar.getInstance().apply { time = dateTime!! }
            val duration = binding.editDuration.text.toString().toInt()

            val newSettings = PumpSettings(
                enabled = binding.switchEnabled.isChecked,
                year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH) + 1,
                day = calendar.get(Calendar.DAY_OF_MONTH),
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                second = 0,
                duration = duration
            )
            onSave(newSettings)
            dismiss()
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi khi lưu cài đặt", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun show(
            context: Context,
            currentSettings: PumpSettings,
            onSave: (PumpSettings) -> Unit
        ) {
            PumpSettingsDialog(context, currentSettings, onSave).show()
        }
    }
}