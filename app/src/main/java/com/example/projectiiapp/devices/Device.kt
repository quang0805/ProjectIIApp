package com.example.projectiiapp.devices

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
@IgnoreExtraProperties
data class Device(
    var deviceId: String = " " ,
    val ownerId: String = " ",
    val name: String = " ",

    @get:PropertyName("sensor_data")
    @set:PropertyName("sensor_data")
    var sensorData: SensorData = SensorData(),

    @get:PropertyName("control")
    @set:PropertyName("control")
    var control: DeviceControl = DeviceControl()
) {
    data class SensorData(
        val humidity: Double = 0.0,
        val temperature: Double = 0.0,
        val timestamp: Long = 0
    )

    data class DeviceControl(
        val ledControl: Boolean = false,
        val pumpControl: Boolean = false,
    )
}