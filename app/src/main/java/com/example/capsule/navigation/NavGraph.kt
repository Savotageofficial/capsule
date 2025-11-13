package com.example.capsule.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capsule.AuthRepository
import com.example.capsule.HomePage
import com.example.capsule.ui.screens.auth.LoginScreen
import com.example.capsule.ui.screens.auth.SignUpScreen
import com.example.capsule.ui.screens.dashboards.DoctorDashboardScreen
import com.example.capsule.ui.screens.profiles.*

@Composable
fun NavGraph(navController: NavHostController) {
    val repository = AuthRepository()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // LOGIN
        composable("login") {
            LoginScreen(
                repository = repository,
                navController = navController,
                onLoginSuccess = { userType ->
                    when (userType.lowercase()) {
                        "patient" -> navController.navigate("patientHome") {
                            popUpTo("login") { inclusive = true }
                        }
                        "doctor" -> navController.navigate("DoctorDashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onSignUpClick = { navController.navigate("signUp") }
            )
        }


        composable("signUp") {
            SignUpScreen(
                repository = repository,
                navController = navController,
                onSignUpSuccess = { userType ->
                    when (userType.lowercase()) {
                        "patient" -> navController.navigate("patientHome") {
                            popUpTo("login") { inclusive = true }
                        }
                        "doctor" -> navController.navigate("DoctorDashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onBackToLogin = { navController.navigate("login") { popUpTo("signUp") { inclusive = true } } }
            )
        }


        // PATIENT HOME
        composable("patientHome") {
            HomePage(
                onProfilePatientClick = {
                    navController.navigate("patientProfile")
                },
//                onAppointmentsClick = {
//                    navController.navigate("appointments") // optional screen
//                },
//                onChatsClick = {
//                    navController.navigate("chats") // optional screen
//                }
            )
        }

        // Patient Profile
        composable("patientProfile") {
            PatientProfileScreen(
                onEditClick = { navController.navigate("editPatientProfile") },
                onBackClick = { navController.popBackStack() }
            )
        }

        // DOCTOR DASHBOARD
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
                onEditClick = { navController.navigate("editDoctorProfile") },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Edit Doctor
        composable("editDoctorProfile") {
            DoctorEditProfileScreen(onSaveClick = { navController.popBackStack() })
        }


        // Edit Patient
        composable("editPatientProfile") {
            PatientEditProfileScreen(onSaveClick = { navController.popBackStack() })
        }
    }
}
