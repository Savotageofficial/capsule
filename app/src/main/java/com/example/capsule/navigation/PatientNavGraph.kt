package com.example.capsule.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capsule.HomePage
import com.example.capsule.ui.screens.profiles.PatientEditProfileScreen
import com.example.capsule.ui.screens.profiles.PatientProfileScreen

@Composable
fun PatientNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier  // add modifier here
) {
    NavHost(
        navController = navController,
        startDestination = "patientHome",
        modifier = Modifier.padding()
    ) {
        composable("patientHome") {
            HomePage(
                onProfilePatientClick = {
                    navController.navigate("patientProfile")
                },
                onAppointmentsClick = {
                    // TODO: Add appointments navigation
                },
                onChatsClick = {
                    // TODO: Add chats navigation
                },
                onSearchClick = {
                    // TODO: Add search navigation
                }
            )
        }

        composable("patientProfile") {
            PatientProfileScreen(
                patientId = null,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate("editPatientProfile") }
            )
        }

        composable("editPatientProfile") {
            PatientEditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }
    }
}