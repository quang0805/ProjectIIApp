package com.example.projectiiapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.projectiiapp.auth.AuthViewModel
import com.example.projectiiapp.R
import com.example.projectiiapp.databinding.FragmentHomeBinding
import com.example.projectiiapp.devices.Device
import com.example.projectiiapp.devices.DeviceViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.nio.charset.StandardCharsets
import kotlin.getValue


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
//    private val deviceViewModel: DeviceViewModel by activityViewModels()
    // Các biến cho MQTT
//    val host: String = "7249966839ac4bf68fc9bb228451bd0b.s1.eu.hivemq.cloud"
//    val username: String = "quang"
//    val password: String = "Quangkk123"
//    val topic: String = "sensor/data"
//    val ledTopic: String = "led/control"
//    val pumpTopic: String = "pump/control"
//    val host: String = "7882f49ec5a24abc9c49b6c8332f73e4.s1.eu.hivemq.cloud"
//    val username : String = "hayson"
//    val password : String = "Alo123,./"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = (childFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment).navController
        binding.bottomNavigationHome.setupWithNavController(navController)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "Plant"
        binding.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.gray_100))


        // Xử lý sự kiện nút back trên Toolbar
            binding.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

//    private fun setupClickListeners() {
//        swLed.setOnCheckedChangeListener { _, isChecked ->
//            val pumpStatus = swPump.isChecked
//            if (isChecked) {
//                deviceViewModel.controlDevice(deviceViewModel.currentDevice.value?.deviceId?:"", true, pumpStatus)
//            } else {
//                deviceViewModel.controlDevice(deviceViewModel.currentDevice.value?.deviceId?:"", false, pumpStatus)
//            }
//        }
//        swPump.setOnCheckedChangeListener { _, isChecked ->
//            val ledStatus = swLed.isChecked
//            if (isChecked) {
//                deviceViewModel.controlDevice(deviceViewModel.currentDevice.value?.deviceId?:"", ledStatus, true)
//            } else {
//                deviceViewModel.controlDevice(deviceViewModel.currentDevice.value?.deviceId?:"", ledStatus, false)
//            }
//        }
//    }

//    @SuppressLint("SetTextI18n")
//    private fun setupObservers(){
//        deviceViewModel.currentDevice.observe(viewLifecycleOwner){
//            binding.txtDisplayDeviceName.text = it?.deviceId
//            swLed.isChecked = it?.control?.ledControl?: false
//            swPump.isChecked = it?.control?.pumpControl ?: false
//            binding.txtHumidity.text = "Humidity: ${it?.sensorData?.humidity.toString()}"
//            binding.txtTemperature.text = "Temperature: ${it?.sensorData?.temperature.toString()}"
////            Toast.makeText(requireContext(), "swLed: ${it?.deviceId}", Toast.LENGTH_SHORT).show()
//        }
//        Log.d("CurrentDeviceId: ", deviceViewModel.currentDevice.value?.deviceId?:"")
//        deviceViewModel.fetchCurrentDevice(deviceViewModel.currentDevice.value?.deviceId)

    }

//    private fun setupClickListeners(){
//        btnConnect.setOnClickListener {
//            btnConnect.isEnabled = false
//            if (!connected) {
//                mqttConnect()
//                btnConnect.text = "Disconnect!"
//                btnConnect.setBackgroundResource(R.drawable.button_clicked)
//                btnConnect.isEnabled = true
//            } else {
//                mqttDisconnect()
//                btnConnect.setText("Connect to MQTT!")
//                btnConnect.setBackgroundResource(R.drawable.button_selector)
//                btnConnect.isEnabled = true
//            }
//            connected = !connected
//        }
//        swLed.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                publishMessage(ledTopic, "ON")
//            } else {
//                publishMessage(ledTopic, "OFF")
//            }
//        }
//        swPump.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                publishMessage(pumpTopic, "ON")
//            } else {
//                publishMessage(pumpTopic, "OFF")
//            }
//        }
//        btnLogout.setOnClickListener {
//            Toast.makeText(requireContext(), "Logout!", Toast.LENGTH_SHORT).show()
//            authViewModel.logout()
//        }
//    }
//    private fun mqttConnect() {
//        client = Mqtt5Client.builder()
//            .identifier("AndroidClient")
//            .serverHost(host)
//            .serverPort(8883)
//            .sslWithDefaultConfig()
//            .build()
//
//        try {
//            client.toBlocking()
//                .connectWith()
//                .simpleAuth()
//                .username(username)
//                .password(password.toByteArray(StandardCharsets.UTF_8))
//                .applySimpleAuth()
//                .send()
//
//            Toast.makeText(requireContext(), "Connected to MQTT!", Toast.LENGTH_SHORT).show()
//
//            client.toAsync().subscribeWith()
//                .topicFilter(topic)
//                .callback { publish ->
//                    val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
//                    activity?.runOnUiThread {
//                        updateSensorData(message)
//                    }
//                }
//                .send()
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "MQTT Connection Failed!", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//    }
//    private fun publishMessage(topic: String, message: String) {
//        try {
//            client.toAsync().publishWith()
//                .topic(topic)
//                .payload(message.toByteArray(StandardCharsets.UTF_8))
//                .send()
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Chưa kết nối với Server!", Toast.LENGTH_SHORT).show()
//        }
//    }
//    private fun updateSensorData(json: String) {
//        try {
//            val jsonObject = org.json.JSONObject(json)
//            val temperature = jsonObject.getDouble("temperature")
//            val humidity = jsonObject.getDouble("humidity")
//
//            txtTemperature.text = "Temperature: $temperature °C"
//            txtHumidity.text = "Humidity: $humidity %"
//        } catch (e: Exception) {
//            txtTemperature.text = "Invalid data"
//            txtHumidity.text = ""
//        }
//    }
//    private fun mqttDisconnect() {
//        client.toBlocking().disconnect()
//        Toast.makeText(requireContext(), "Disconnected", Toast.LENGTH_SHORT).show()
//        txtTemperature.text = "Temperature: 0 °C"
//        txtHumidity.text = "Humidity: 0 %"
//    }
//}
