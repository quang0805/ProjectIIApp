package com.example.projectiiapp
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.projectiiapp.auth.AuthState
import com.example.projectiiapp.auth.AuthViewModel
import com.example.projectiiapp.devices.DeviceViewModel

class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val deviceViewModel: DeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Success -> {
                    if (navController.currentDestination?.id != R.id.listDeviceFragment)
                        navController.navigate(R.id.listDeviceFragment)
                }
                is AuthState.LoggedOut -> {
                    navController.popBackStack(R.id.loginFragment, false)
                }
                else -> Unit
            }
        }
    }




}




