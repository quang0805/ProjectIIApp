package com.example.projectiiapp.devices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectiiapp.R


class DeviceAdapter(
    private val onItemClick: (Device) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    private val devices = mutableListOf<Device>()

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDeviceId: TextView = itemView.findViewById(R.id.tvDeviceId)
        private val tvDeviceName: TextView = itemView.findViewById(R.id.tvDeviceName)

        fun bind(device: Device) {
            tvDeviceId.text = "ID: ${device.deviceId.take(6)}" // Hiển thị 6 ký tự đầu
            tvDeviceName.text = device.name

            itemView.setOnClickListener { onItemClick(device) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount() = devices.size

    fun submitList(newList: List<Device>) {
        devices.clear()
        devices.addAll(newList)
        notifyDataSetChanged()
    }
}