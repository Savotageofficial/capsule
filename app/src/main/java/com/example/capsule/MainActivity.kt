// MainActivity.kt
package com.example.capsule

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.capsule.navigation.NavGraph
import com.example.capsule.ui.theme.CapsuleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userType = intent.getStringExtra("userType") ?: "Patient"

        setContent {
            CapsuleTheme {
                MainApp(userType = userType, onLogout = {
                    // Navigate back to login
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

@Composable
fun MainApp(userType: String, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val startDestination = remember(userType) {
        if (userType == "Doctor") "DoctorDashboard" else "patientHome"
    }

    NavGraph(
        navController = navController,
        startDestination = startDestination,
        onLogout = onLogout
    )
}