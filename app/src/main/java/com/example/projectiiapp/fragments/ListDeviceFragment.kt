package com.example.projectiiapp.fragments
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
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
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, backPressedCallback)
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    private fun setupToolbar() {
        binding.toolbar.title = "Welcome back user!"
        binding.toolbar.inflateMenu(R.menu.menu_toolbar)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton("OK") { _, _ ->
                            authViewModel.logout()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                    true
                }
                else -> false
            }
        }
    }
    private fun setupRecyclerView() {
        // Xu ly khi Click vao Device item, add callback vao DeviceAdapter.
        deviceAdapter = DeviceAdapter { device ->
            navigateToDeviceDetail(device.deviceId)
        }

        binding.rvDevices.setItemViewCacheSize(20)
        binding.rvDevices.setHasFixedSize(true)

        binding.rvDevices.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = deviceAdapter
        }
        deviceAdapter.notifyDataSetChanged()
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

    private fun navigateToDeviceDetail(deviceId: String) {
        viewModel.addCurrentDevice(deviceId)
//        viewModel.fetchCurrentDevice(deviceId)
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true) // Ngăn tạo instance mới nếu Fragment đã tồn tại
            .setPopUpTo(R.id.listDeviceFragment, false) // Không pop Fragment hiện tại
            .build()
        findNavController().navigate(R.id.action_listDeviceFragment_to_homeFragment, null, navOptions)
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
    // Khi thoat thi thoat luon ra ngoai man hinh
    private val backPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            handleBackPressed()
        }
    }
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