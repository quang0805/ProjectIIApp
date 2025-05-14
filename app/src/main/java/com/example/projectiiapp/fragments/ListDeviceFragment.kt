package com.example.projectiiapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projectiiapp.R
import com.example.projectiiapp.auth.AuthViewModel
import com.example.projectiiapp.databinding.FragmentListDeviceBinding
import com.example.projectiiapp.devices.DeviceAdapter
import com.example.projectiiapp.devices.DeviceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ListDeviceFragment : Fragment() {
    private lateinit var binding: FragmentListDeviceBinding
    private val authViewModel: AuthViewModel by activityViewModels()
    private val viewModel: DeviceViewModel by activityViewModels()
    private lateinit var deviceAdapter: DeviceAdapter

    // Trong fragment nay, khi backPress thi thoat ra ngoai man hinh
    private val backPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            handleBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListDeviceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, backPressedCallback)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "Welcome back user!"
    }
    private fun setupRecyclerView() {
        // Xu ly khi Click vao Device item, add callback vao DeviceAdapter.
        deviceAdapter = DeviceAdapter { device ->
            Toast.makeText(requireContext(), "Clicked on ${device.name}", Toast.LENGTH_SHORT).show()
            navigateToDeviceDetail(device.deviceId)
        }


        binding.rvDevices.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = deviceAdapter
//            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
    private fun setupObservers() {
        // Hiển thị tên người dùng
        Firebase.auth.currentUser?.email?.let { email ->
            binding.tvUserName.text = email.substringBefore("@")
        }

        // Load danh sách thiết bị
        viewModel.devices.observe(viewLifecycleOwner) { devices ->
            deviceAdapter.submitList(devices) // Khi có thiết bị được thêm vào thì cập nhật danh sách
        }

        viewModel.fetchDevices()
    }
    private fun setupClickListeners() {
        binding.fabAddDevice.setOnClickListener {
            showAddDeviceDialog()
        }
    }
    private fun showAddDeviceDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Device")
            .setView(R.layout.dialog_add_device)
            .setPositiveButton("Add") { dialog, _ ->
                // Xử lý thêm thiết bị
                val input =
                    (dialog as androidx.appcompat.app.AlertDialog).findViewById<TextInputEditText>(R.id.edtDeviceName)
                input?.text?.toString()?.let { deviceName ->
                    viewModel.addDevice(deviceName)
                    viewModel.fetchDevices()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun navigateToDeviceDetail(deviceId: String) {
        viewModel.addCurrentDevice(deviceId)
        findNavController().navigate(R.id.action_listDeviceFragment_to_homeFragment)
    }



    // Khi thoat thi thoat luon ra ngoai man hinh
    private fun handleBackPressed() {
        AlertDialog.Builder(requireContext())
            .setTitle("Thoát ứng dụng")
            .setMessage("Bạn có chắc chắn muốn thoát?")
            .setPositiveButton("Thoát") { _, _ ->
                requireActivity().finishAffinity()
            }
            .setNegativeButton("Ở lại", null)
            .show()
    }

}