package com.example.capsule.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.ui.screens.doctor.DoctorDashboardScreen
import com.example.capsule.ui.screens.doctor.DoctorEditProfileScreen
import com.example.capsule.ui.screens.doctor.DoctorProfileScreen
import com.example.capsule.ui.screens.doctor.DoctorScheduleScreen
import com.example.capsule.ui.screens.doctor.DoctorViewModel
import com.example.capsule.ui.screens.doctor.ViewDoctorProfileScreen
import com.example.capsule.ui.screens.booking.BookingConfirmationScreen
import com.example.capsule.ui.screens.search.SearchScreen
import com.example.capsule.ui.screens.search.SearchResultsScreen
import com.example.capsule.ui.screens.settings.SettingsScreen
import com.example.capsule.ui.screens.patient.HomePage
import com.example.capsule.ui.screens.patient.PatientAppointmentsScreen
import com.example.capsule.ui.screens.patient.PatientEditProfileScreen
import com.example.capsule.ui.screens.patient.PatientProfileScreen
import com.example.capsule.ui.screens.patient.PatientViewModel
import com.example.capsule.ui.screens.patient.ViewPatientProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onLogout: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = startDestination) {

        // Patient Home
        composable("patientHome") {
            HomePage(
                onProfilePatientClick = { navController.navigate("patientProfile") },
                onSearchClick = { navController.navigate("search") },
                onSettingsClick = { navController.navigate("settings") },
                onAppointmentsClick = { navController.navigate("patientAppointments") }
            )
        }

        composable("patientProfile") {
            PatientProfileScreen(
                onEditClick = { navController.navigate("editPatientProfile") },
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate("settings") }

            )
        }

        composable("editPatientProfile") {
            PatientEditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        composable("patientAppointments") {
            PatientAppointmentsScreen(onBackClick = { navController.popBackStack() })
        }

        // Doctor Dashboard
        composable("DoctorDashboard") {
            DoctorDashboardScreen(
                onProfileClick = { navController.navigate("doctorProfile") },
                onPatientClick = { patientId ->
                    navController.navigate("viewPatientProfile/$patientId")
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

        composable("doctorSchedule") {
            DoctorScheduleScreen(
                onBackClick = { navController.popBackStack() },
                onPatientClick = { patientId -> navController.navigate("viewPatientProfile/$patientId") }
            )
        }

        // SearchScreen Routes
        composable("search") {
            SearchScreen(
                searchResults = emptyList(),
                onDoctorSelected = { doctorId ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("doctorId", doctorId)
                    navController.navigate("searchResults")
                },
                onBackClick = { navController.popBackStack() },
                navController = navController
            )
        }

        composable("searchResults") {
            SearchResultsScreen(navController = navController)
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = onLogout
            )
        }

        // View Profiles
        composable("viewDoctorProfile/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            val patientViewModel: PatientViewModel = viewModel()

            ViewDoctorProfileScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() },
                onBookingSuccess = { time, slot, type ->
                    navController.navigate("bookingConfirmation/$doctorId/$time/${slot.start}/${slot.end}/$type")
                },
                patientViewModel = patientViewModel
            )
        }

        composable("viewPatientProfile/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            ViewPatientProfileScreen(
                patientId = patientId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Booking
        composable("bookingConfirmation/{doctorId}/{timestamp}/{slotStart}/{slotEnd}/{type}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            val timestamp = backStackEntry.arguments?.getString("timestamp")?.toLongOrNull() ?: 0L
            val slotStart = backStackEntry.arguments?.getString("slotStart") ?: ""
            val slotEnd = backStackEntry.arguments?.getString("slotEnd") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: "In-Person"

            val doctorViewModel: DoctorViewModel = viewModel()

            BookingConfirmationScreen(
                doctorId = doctorId,
                selectedDate = timestamp,
                selectedSlot = TimeSlot(slotStart, slotEnd),
                appointmentType = type,
                onBackToHome = {
                    navController.navigate("patientHome") {
                        popUpTo(0)
                    }
                },
                doctorViewModel = doctorViewModel
            )
        }
    }
}
