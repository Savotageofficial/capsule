package com.example.capsule.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capsule.ui.screens.*
import com.example.capsule.ui.screens.profiles.DoctorProfileScreen
import com.example.capsule.ui.screens.profiles.PatientEditProfileScreen
import com.example.capsule.ui.screens.profiles.PatientProfileScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"  // starting screen
    ) {

        // Doctor Profile
        composable("doctorProfile") {
            DoctorProfileScreen(
                onEditClick = { navController.navigate("editDoctorProfile") },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Edit Doctor Profile
        composable("editDoctorProfile") {
            DoctorEditProfileScreen(
                onSaveClick = { navController.popBackStack() }
            )
        }

        // Patient Profile
        composable("patientProfile") {
            PatientProfileScreen(
                onEditClick = { navController.navigate("editPatientProfile") },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Edit Patient Profile
        composable("editPatientProfile") {
            PatientEditProfileScreen(
                onSaveClick = {/*firebase update*/ },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
