package com.example.projectiiapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.example.projectiiapp.R
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.nio.charset.StandardCharsets

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var txtTemperature: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnLedOn: Button
    private lateinit var btnLedOff: Button
    private lateinit var swLed: Switch
    private lateinit var swPump: Switch

    private lateinit var client: Mqtt5Client
    //val host: String = "7249966839ac4bf68fc9bb228451bd0b.s1.eu.hivemq.cloud"
    //val username : String = "quang"
    //val password : String = "Quangkk123"

    val topic : String = "sensor/data"
    val ledTopic: String = "led/control"
    val pumpTopic: String = "pump/control"
    val host: String = "7882f49ec5a24abc9c49b6c8332f73e4.s1.eu.hivemq.cloud"
    val username : String = "hayson"
    val password : String = "Alo123,./"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        var connected = false


        btnConnect = view.findViewById(R.id.btnConnect)
        txtTemperature = view.findViewById(R.id.txtTemperature)
        txtHumidity = view.findViewById(R.id.txtHumidity)
        swLed = view.findViewById<Switch>(R.id.swLed)
        swPump = view.findViewById<Switch>(R.id.swPump)


        btnConnect.setOnClickListener {
            btnConnect.isEnabled = false
            if(!connected){
                mqttConnect()
                btnConnect.text = "Disconnect!"
                btnConnect.setBackgroundResource(R.drawable.button_clicked)
                btnConnect.isEnabled = true
            }else{
                mqttDisconnect()
                btnConnect.setText("Connect to MQTT!")
                btnConnect.setBackgroundResource(R.drawable.button_selector)
                btnConnect.isEnabled = true
            }
            connected = !connected
        }
        swLed.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                publishMessage(ledTopic, "ON")
            }else{
                publishMessage(ledTopic, "OFF")
            }
        }
        swPump.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                publishMessage(pumpTopic, "ON")
            } else {
                publishMessage(pumpTopic, "OFF")
            }
        }



        return view
    }
    private fun mqttConnect() {
        client = Mqtt5Client.builder()
            .identifier("AndroidClient")
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .build()


        try{
            client.toBlocking()
                .connectWith()
                .simpleAuth()
                .username(username)
                .password(password.toByteArray(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .send()

            Toast.makeText(requireContext(), "Connected to MQTT!", Toast.LENGTH_SHORT).show()

            client.toAsync().subscribeWith()
                .topicFilter(topic)
                .callback { publish ->
                    val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                    activity?.runOnUiThread {
                        updateSensorData(message)
                    }
                }
                .send()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "MQTT Connection Failed!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun publishMessage(topic: String, message: String) {
        try {
            client.toAsync().publishWith()
                .topic(topic)
                .payload(message.toByteArray(StandardCharsets.UTF_8))
                .send()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Kiểm tra MQTT đã được kết nối hay chưa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSensorData(json: String) {
        try {
            val jsonObject = org.json.JSONObject(json)
            val temperature = jsonObject.getDouble("temperature")
            val humidity = jsonObject.getDouble("humidity")

            txtTemperature.text = "Temperature: $temperature °C"
            txtHumidity.text = "Humidity: $humidity %"
        } catch (e: Exception) {
            txtTemperature.text = "Invalid data"
            txtHumidity.text = ""
        }
    }
    private fun mqttDisconnect() {
        client.toBlocking().disconnect()
        Toast.makeText(requireContext(), "Disconnected", Toast.LENGTH_SHORT).show()
        txtTemperature.text = "Temperature: 0 °C"
        txtHumidity.text = "Humidity: 0 %"
    }







    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}