package com.example.projectiiapp
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private lateinit var txtTemperature: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnLedOn: Button
    private lateinit var btnLedOff: Button

    private lateinit var client: Mqtt5Client

    //val host: String = "7249966839ac4bf68fc9bb228451bd0b.s1.eu.hivemq.cloud"
    //val username : String = "quang"
    //val password : String = "Quangkk123"
    val topic : String = "sensor/data"
    val ledTopic: String = "led/control"
    val host: String = "7882f49ec5a24abc9c49b6c8332f73e4.s1.eu.hivemq.cloud"
    val username : String = "hayson"
    val password : String = "Alo123,./"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var connected = false


        btnConnect = findViewById(R.id.btnConnect)
        txtTemperature = findViewById(R.id.txtTemperature)
        txtHumidity = findViewById(R.id.txtHumidity)
        btnLedOn = findViewById(R.id.btnLedOn)
        btnLedOff = findViewById(R.id.btnLedOff)

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

        btnLedOn.setOnClickListener {
            publishMessage(ledTopic, "ON")
        }


        btnLedOff.setOnClickListener {
            publishMessage(ledTopic, "OFF")
        }

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

            Toast.makeText(this, "Connected to MQTT!", Toast.LENGTH_SHORT).show()

            client.toAsync().subscribeWith()
                .topicFilter(topic)
                .callback { publish ->
                    val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                    runOnUiThread {
                        updateSensorData(message)
                    }
                }
                .send()
        } catch (e: Exception) {
            Toast.makeText(this, "MQTT Connection Failed!", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Kiểm tra MQTT đã được kết nối hay chưa", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
        txtTemperature.text = "Temperature: 0 °C"
        txtHumidity.text = "Humidity: 0 %"
    }


}




