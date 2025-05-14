package com.example.projectiiapp.devices

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class DeviceViewModel: ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val userId = Firebase.auth.currentUser?.uid ?: ""

    private val _devices = MutableLiveData<List<Device>>()
    val devices: LiveData<List<Device>> = _devices

    private val _currentDevice = MutableLiveData<Device?>()
    val currentDevice: LiveData<Device?> = _currentDevice

    fun fetchDevices(){
        database.child("users/$userId/devices").addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val deviceIds = snapshot.children.map { it.key }
                    val deviceList = mutableListOf<Device>()

                    deviceIds.forEach { deviceId ->
                        database.child("devices/$deviceId").get()
                            .addOnSuccessListener { deviceSnapshot ->
                                deviceSnapshot.getValue(Device::class.java)?.let {
                                    deviceList.add(it.copy(deviceId = deviceId!!))
                                    _devices.value = deviceList
                                }
                            }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("DeviceVM", "Error fetching devices", error.toException())
                }
            }
        )
    }

    fun addDevice(deviceName: String){
        val key = database.child("devices").push().key
        if (key == null){
            Log.e("DeviceVM", "Key is null")
            return
        }

        val device = Device(
            deviceId = key,
            ownerId = userId,
            name = deviceName.trim()
        )

        val updates = hashMapOf<String, Any>(
            "devices/$key" to device,
            "users/$userId/devices/$key" to true
        )
        database.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("DeviceVM", "Thêm thiết bị thành công")
            }
            .addOnFailureListener { e ->
                Log.e("DeviceVM", "Lỗi khi thêm thiết bị", e)
            }
    }

    fun addCurrentDevice(deviceId: String) {
        database.child("devices/$deviceId").get()
            .addOnSuccessListener { deviceSnapshot ->
                deviceSnapshot.getValue(Device::class.java)?.let {
                    _currentDevice.value = it.copy(deviceId = deviceId)
                }
            }
    }
    fun controlDevice(deviceId: String, ledState: Boolean, pumpState: Boolean) {
        val updates = mapOf(
            "control/ledControl" to ledState,
            "control/pumpControl" to pumpState,
            "control/last_updated" to ServerValue.TIMESTAMP
        )
        database.child("devices/$deviceId").updateChildren(updates)
    }
}