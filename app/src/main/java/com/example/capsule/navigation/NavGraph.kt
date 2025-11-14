package com.example.capsule.navigation

import  androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capsule.HomePage
import com.example.capsule.ui.screens.dashboards.DoctorDashboardScreen
import com.example.capsule.ui.screens.profiles.DoctorEditProfileScreen
import com.example.capsule.ui.screens.profiles.DoctorProfileScreen
import com.example.capsule.ui.screens.profiles.PatientEditProfileScreen
import com.example.capsule.ui.screens.profiles.PatientProfileScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"  // starting screen
    ) {

        // Patient Home
        composable("patientHome") {
            HomePage(
                onProfilePatientClick =  { navController.navigate("patientProfile") },
                searchonclick = {}
            )
        }

        // Doctor Dashboard
        composable("DoctorDashboard") {
            DoctorDashboardScreen(
                onProfileClick =  { navController.navigate("doctorProfile") },
                onPatientClick = { patientName ->
                    navController.navigate("patientProfile/$patientName")
                }
            )
        }

        // Doctor Profile
        composable("doctorProfile") {
            DoctorProfileScreen(
                onEditClick = { navController.navigate("editDoctorProfile") },
                onBackClick = { navController.popBackStack() },
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
                onSaveClick = {
                    // TODO: make firebase repo file and and paste this in it
                    /*
                    val db = FirebaseFirestore.getInstance()
                    val userId = FirebaseAuth.getInstance().currentUser?.uid

                    if (userId != null) {
                        val updatedDoctor = mapOf(
                            "name" to name.text,
                            "specialty" to specialty.text,
                            "bio" to bio.text,
                            "experience" to experience.text,
                            "clinicName" to clinicName.text,
                            "clinicAddress" to clinicAddress.text,
                            "availability" to availability.text
                        )

                        db.collection("doctors")
                            .document(userId)
                            .update(updatedDoctor)
                            .addOnSuccessListener {
                                Log.d("DoctorEditProfile", "Doctor profile updated.")
                                showSaveDialog = true
                            }
                            .addOnFailureListener { e ->
                                Log.e("DoctorEditProfile", "Error updating profile", e)
                            }
                    }
                    */
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

//ignore (by safwat)