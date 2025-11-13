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
                NavGraph(navController = navController) // all screens are handled here
            }
        }
    }
}