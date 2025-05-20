package com.example.projectiiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class HiveMqViewModel: ViewModel() {
    val host: String = "7249966839ac4bf68fc9bb228451bd0b.s1.eu.hivemq.cloud"
    val username: String = "quang"
    val password: String = "Quangkk123"
    val topic: String = "sensor/data"
//    val pumpTopic: String = "pump/control"
//    val pumpSchedule: String = "pump/schedule"

//    val host: String = "7882f49ec5a24abc9c49b6c8332f73e4.s1.eu.hivemq.cloud"
//    val username : String = "hayson"
//    val password : String = "Alo123,./"

    private val _connectionStatus = MutableLiveData<Boolean>(false)
    val connectionStatus: LiveData<Boolean> = _connectionStatus

    private val _pumpStatus = MutableLiveData<Boolean>(false)
    val pumpStatus: LiveData<Boolean> = _pumpStatus

    private val _sensorData = MutableLiveData<SensorData>()
    val sensorData: LiveData<SensorData> = _sensorData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private var client: Mqtt5Client? = null

    init {
        mqttConnect()
        getDataSensor()
    }

    data class SensorData(
        val temperature: Float = 0f,
        val airHumidity: Float = 0f,
        val soilMoisture: Float = 0f,
    )
    private fun mqttConnect() {
        client = Mqtt5Client.builder()
            .identifier("AndroidClient")
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .build()

        try {
            client?.toBlocking()
                ?.connectWith()
                ?.simpleAuth()
                ?.username(username)
                ?.password(password.toByteArray(StandardCharsets.UTF_8))
                ?.applySimpleAuth()
                ?.send()

//            Toast.makeText(requireContext(), "Connected to MQTT!", Toast.LENGTH_SHORT).show()
            _connectionStatus.postValue(true)


            client?.toAsync()?.subscribeWith()
                ?.topicFilter(topic)
                ?.callback { publish ->
                    val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                }
                ?.send()
        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "MQTT Connection Failed!", Toast.LENGTH_SHORT).show()
            _connectionStatus.postValue(false)
            _errorMessage.postValue("MQTT Connection Failed!")
            e.printStackTrace()
        }
    }

    private fun getDataSensor() {
        try {
            client?.toAsync()?.subscribeWith()
                ?.topicFilter(topic)
                ?.callback { publish ->
                    val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                    val jsonObject = JSONObject(message)
                    val temperature = jsonObject.getDouble("temperature").toFloat()
                    val airHumidity = jsonObject.getDouble("humidity").toFloat()
                    val soilMoisture = jsonObject.getDouble("soil").toFloat()
                    _sensorData.postValue(SensorData(temperature, airHumidity, soilMoisture))
                }
                ?.send()
        } catch (e: Exception) {
            e.printStackTrace()
        }
}


    fun publishMessage(topic: String, message: String) {
        try {
            client?.toAsync()?.publishWith()
                ?.topic(topic)
                ?.payload(message.toByteArray(StandardCharsets.UTF_8))
                ?.send()
        } catch (e: Exception) {
            _errorMessage.postValue("Chưa kết nối với Server!")
        }
    }
    private fun mqttDisconnect() {
        client?.toBlocking()?.disconnect()
//        Toast.makeText(requireContext(), "Disconnected", Toast.LENGTH_SHORT).show()
        _connectionStatus.postValue(false)
    }

}