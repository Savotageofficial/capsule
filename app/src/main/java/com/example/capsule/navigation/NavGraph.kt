// NavGraph.kt (updated)
package com.example.capsule.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capsule.ui.screens.patient.HomePage
import com.example.capsule.ui.screens.features.Search
import com.example.capsule.ui.screens.features.SettingsScreen
import com.example.capsule.ui.screens.doctor.DoctorDashboardScreen
import com.example.capsule.ui.screens.doctor.DoctorEditProfileScreen
import com.example.capsule.ui.screens.doctor.DoctorProfileScreen
import com.example.capsule.ui.screens.doctor.DoctorScheduleScreen
import com.example.capsule.ui.screens.doctor.ViewDoctorProfileScreen
import com.example.capsule.ui.screens.patient.PatientEditProfileScreen
import com.example.capsule.ui.screens.patient.PatientProfileScreen
import com.example.capsule.ui.screens.patient.ViewPatientProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onLogout: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Patient Home
        composable("patientHome") {
            HomePage(
                onProfilePatientClick = { navController.navigate("patientProfile") },
                onSearchClick = { navController.navigate("search") },
                onSettingsClick = { navController.navigate("settings") },
                )
        }

        // Search Screen
        composable("search") {
            Search(
                searchResults = listOf("mohamed safwat", "mohamed hany", "youssef ahmed", "hamza hesham"),
                onBackClick = { navController.popBackStack() }
            )
        }

        // Settings Screen
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = onLogout
            )
        }

        // Doctor Dashboard
        composable("DoctorDashboard") {
            DoctorDashboardScreen(
                onProfileClick = { navController.navigate("doctorProfile") },
                onPatientClick = { patientId ->
                    navController.navigate("patientProfile/$patientId")
                },
                onScheduleClick = { navController.navigate("doctorSchedule") },
                onSettingsClick = { navController.navigate("settings") },
                onMessagesClick = { /* TODO */ },
                onPrescriptionClick = { /* TODO */ }
            )
        }

        // Doctor Profile
        composable("doctorProfile") {
            DoctorProfileScreen(
                doctorId = null,
                onEditClick = { navController.navigate("editDoctorProfile") },
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate("settings") }
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
                patientId = null,
                onEditClick = { navController.navigate("editPatientProfile") },
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        // Edit Patient Profile
        composable("editPatientProfile") {
            PatientEditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        // View Doctor Profile
        composable("viewDoctorProfile/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            ViewDoctorProfileScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // View Patient Profile
        composable("viewPatientProfile/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            ViewPatientProfileScreen(
                patientId = patientId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Doctor Schedule
        composable("doctorSchedule") {
            DoctorScheduleScreen(
                onBackClick = { navController.popBackStack() },
                onPatientClick = { patientId ->
                    navController.navigate("viewPatientProfile/$patientId")
                }
            )
        }
    }
}