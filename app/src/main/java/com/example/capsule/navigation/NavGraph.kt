package com.example.capsule.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.ui.screens.doctor.*
import com.example.capsule.ui.screens.booking.BookingConfirmationScreen
import com.example.capsule.ui.chat.ChatSelectionScreen
import com.example.capsule.ui.screens.appointments.DoctorScheduleScreen
import com.example.capsule.ui.screens.appointments.PatientAppointmentsScreen
import com.example.capsule.ui.screens.dashboards.DoctorDashboardScreen
import com.example.capsule.ui.screens.dashboards.HomePage
import com.example.capsule.ui.screens.search.SearchScreen
import com.example.capsule.ui.screens.search.SearchResultsScreen
import com.example.capsule.ui.screens.settings.SettingsScreen
import com.example.capsule.ui.screens.patient.*
import com.example.capsule.ui.screens.prescription.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onLogout: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = startDestination) {

        // PATIENT ROUTES
        composable("patientHome") {
            HomePage(
                onProfilePatientClick = { navController.navigate("patientProfile") },
                onSearchClick = { navController.navigate("search") },
                onSettingsClick = { navController.navigate("settings") },
                onAppointmentsClick = { navController.navigate("patientAppointments") },
                onPrescriptionsClick = { navController.navigate("patientPrescriptions") },
                onMessagesClick = { navController.navigate("chatSelection") }  // Added
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
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("patientAppointments") {
            PatientAppointmentsScreen(
                onBackClick = { navController.popBackStack() },
                onDoctorClick = { doctorId ->
                    navController.navigate("viewDoctorProfile/$doctorId")
                }
            )
        }

        // Patient Prescriptions
        composable("patientPrescriptions") {
            PatientPrescriptionsScreen(
                onBackClick = { navController.popBackStack() },
                onViewPrescription = { prescriptionId ->
                    navController.navigate("viewPrescription/$prescriptionId/false")
                }
            )
        }

        // DOCTOR ROUTES
        composable("DoctorDashboard") {
            DoctorDashboardScreen(
                onProfileClick = { navController.navigate("doctorProfile") },
                onPatientClick = { patientId ->
                    navController.navigate("viewPatientProfile/$patientId")
                },
                onScheduleClick = { navController.navigate("doctorSchedule") },
                onSettingsClick = { navController.navigate("settings") },
                onMessagesClick = { navController.navigate("chatSelection") },  // Updated
                onPrescriptionClick = {
                    navController.navigate("doctorPrescriptions")
                },
                onMakePrescriptionClick = {
                    navController.navigate("makePrescription")
                }
            )
        }

        composable("doctorProfile") {
            DoctorProfileScreen(
                onEditClick = { navController.navigate("editDoctorProfile") },
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        composable("editDoctorProfile") {
            DoctorEditProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("doctorSchedule") {
            DoctorScheduleScreen(
                onBackClick = { navController.popBackStack() },
                onPatientClick = { patientId -> navController.navigate("viewPatientProfile/$patientId") }
            )
        }

        // Doctor Prescriptions List
        composable("doctorPrescriptions") {
            DoctorPrescriptionsScreen(
                onBackClick = { navController.popBackStack() },
                onViewPrescription = { prescriptionId ->
                    navController.navigate("viewPrescription/$prescriptionId/true")
                },
                onNewPrescription = {
                    navController.navigate("makePrescription")
                }
            )
        }

        // Make New Prescription (Doctor only)
        composable("makePrescription") {
            MakeNewPrescriptionScreen(
                onBack = { navController.popBackStack() },
                onSavePrescription = {
                    navController.popBackStack()
                }
            )
        }

        composable("makePrescription/{patientId}/{patientName}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            val patientName = backStackEntry.arguments?.getString("patientName") ?: ""

            MakeNewPrescriptionScreen(
                patientId = patientId,
                patientName = patientName,
                onBack = { navController.popBackStack() },
                onSavePrescription = {
                    navController.popBackStack()
                }
            )
        }

        // View Prescription (Shared - for both patient and doctor)
        composable("viewPrescription/{prescriptionId}/{isDoctorView}") { backStackEntry ->
            val prescriptionId = backStackEntry.arguments?.getString("prescriptionId") ?: ""
            val isDoctorView = backStackEntry.arguments?.getString("isDoctorView")?.toBoolean() ?: false

            ViewPrescriptionScreen(
                prescriptionId = prescriptionId,
                isDoctorView = isDoctorView,
                onBackClick = { navController.popBackStack() }
            )
        }

        // CHAT SELECTION SCREEN
        composable("chatSelection") {
            ChatSelectionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // SEARCH & BOOKING ROUTES
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
                onBackClick = { navController.popBackStack() },
                onLogout = onLogout
            )
        }

        // View Profiles
        composable("viewDoctorProfile/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            ViewDoctorProfileScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() },
                onBookingSuccess = { time, slot, type ->
                    navController.navigate("bookingConfirmation/$doctorId/$time/${slot.start}/${slot.end}/$type")
                }
            )
        }

        composable("viewPatientProfile/{patientId}") { backStackEntry ->
            val patientIdArg = backStackEntry.arguments?.getString("patientId")

            ViewPatientProfileScreen(
                patientId = patientIdArg,
                onBackClick = { navController.popBackStack() },
                onMakePrescriptionClick = { patientId, patientName ->
                    navController.navigate("makePrescription/$patientId/$patientName")
                }
            )
        }

        // Booking Confirmation
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