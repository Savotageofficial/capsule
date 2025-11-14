package com.example.capsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.capsule.navigation.NavGraph
import com.example.capsule.ui.theme.CapsuleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CapsuleTheme {
                val navController = rememberNavController()
                val userType = intent.getStringExtra("userType") ?: "Patient"
                val startDestination = if (userType == "Doctor") "DoctorDashboard" else "patientHome"

                // This should work now
                NavGraph(navController = navController, startDestination = startDestination)
            }
        }
    }
}