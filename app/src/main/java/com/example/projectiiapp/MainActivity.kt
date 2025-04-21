package com.example.projectiiapp
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Success -> {
                    if (navController.currentDestination?.id != R.id.homeFragment)
                        navController.navigate(R.id.homeFragment)
                }
                is AuthState.LoggedOut -> {
                    navController.popBackStack(R.id.loginFragment, false)
                }
                is AuthState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }


    }




}




