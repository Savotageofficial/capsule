package com.example.capsule.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capsule.HomePage
import com.example.capsule.ui.screens.dashboards.DoctorDashboardScreen
import com.example.capsule.ui.screens.features.DoctorScheduleScreen
import com.example.capsule.ui.screens.profiles.DoctorEditProfileScreen
import com.example.capsule.ui.screens.profiles.DoctorProfileScreen
import com.example.capsule.ui.screens.profiles.PatientEditProfileScreen
import com.example.capsule.ui.screens.profiles.PatientProfileScreen
import com.example.capsule.ui.screens.profiles.ViewDoctorProfileScreen
import com.example.capsule.ui.screens.profiles.ViewPatientProfileScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String ) {

    NavHost(
        navController = navController,
        startDestination = startDestination  // Home
    ) {
        // Patient Home
        composable("patientHome") {
            HomePage(
                onProfilePatientClick = { navController.navigate("patientProfile") },
            )
        }

        // Doctor Dashboard
        composable("DoctorDashboard") {
            DoctorDashboardScreen(
                onProfileClick = { navController.navigate("doctorProfile") },
                onPatientClick = { patientName ->
                    navController.navigate("patientProfile/$patientName")
                }
            )
        }

        // Doctor Profile
        composable("doctorProfile") {
            DoctorProfileScreen(
                doctorId = null, // This will load current doctor
                onEditClick = { navController.navigate("editDoctorProfile") },
                onBackClick = { navController.popBackStack() },
            )
        }

        // Edit Doctor Profile
        composable("editDoctorProfile") {
            DoctorEditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        // Patient Profile
        composable("patientProfile") {
            PatientProfileScreen(
                patientId = null, // This will load current patient
                onEditClick = { navController.navigate("editPatientProfile") },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Edit Patient Profile
        composable("editPatientProfile") {
            PatientEditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        // Add view doctor profile route
        composable("viewDoctorProfile/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            ViewDoctorProfileScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Add view patient profile route
        composable("viewPatientProfile/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            ViewPatientProfileScreen(
                patientId = patientId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Add doctor schedule route
        composable("doctorSchedule") {
            DoctorScheduleScreen(
                onBackClick = { navController.popBackStack() },
                onAddSlotClick = { /* Navigate to add slot screen */ },
                onPatientClick = { patientName ->
                    navController.navigate("viewPatientProfile/$patientName")
                }
            )
        }
    }
}