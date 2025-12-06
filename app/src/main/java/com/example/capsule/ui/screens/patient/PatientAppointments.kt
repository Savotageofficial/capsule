package com.example.capsule.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.data.model.Appointment
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen(
    onBackClick: () -> Unit = {},
    onViewAppointmentDetails: (Appointment) -> Unit = {},
    viewModel: PatientViewModel = viewModel()
) {
    val appointments = viewModel.appointments.value
    val isLoading by viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadPatientAppointments()
        // Load patient profile first to ensure we have patient ID
        viewModel.loadCurrentPatientProfile()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_appointments),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Cyan,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Teal
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (appointments.isEmpty()) {
                EmptyAppointmentsState()
            } else {
                AppointmentsList(
                    appointments = appointments,
                    onAppointmentClick = onViewAppointmentDetails,
                    onCancelAppointment = { appointment ->
                        viewModel.cancelAppointment(appointment.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyAppointmentsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = "No appointments",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Text(
                text = stringResource(R.string.no_upcoming_appointments),
                color = Color.Gray,
                fontSize = 16.sp
            )
            Text(
                text = stringResource(R.string.book_your_first_appointment_with_a_doctor),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun AppointmentsList(
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit,
    onCancelAppointment: (Appointment) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appointment = appointment,
                onClick = { onAppointmentClick(appointment) },
                onCancelClick = { onCancelAppointment(appointment) }
            )
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with doctor info and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dr. ${appointment.doctorName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = appointment.type,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .background(
                            color = when (appointment.status) {
                                "Upcoming" -> Color(0x3300FF00)
                                "Completed" -> Color(0x330000FF)
                                "Cancelled" -> Color(0x33FF0000)
                                else -> Color(0x33CCCCCC)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = appointment.status,
                        color = when (appointment.status) {
                            "Upcoming" -> Color(0xFF00C853)
                            "Completed" -> Color(0xFF2196F3)
                            "Cancelled" -> Color(0xFFF44336)
                            else -> Color.Gray
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Appointment details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Date",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatAppointmentDateTime(appointment),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions - Only show cancel for upcoming appointments
            if (appointment.status == "Upcoming") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Cancel Appointment")
                    }
                }
            }
        }
    }

    // Cancel confirmation dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Appointment") },
            text = {
                Text("Are you sure you want to cancel this appointment with Dr. ${appointment.doctorName}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelClick()
                        showCancelDialog = false
                    }
                ) {
                    Text("Yes, Cancel", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Appointment")
                }
            }
        )
    }
}

// Helper function to format appointment date and time
private fun formatAppointmentDateTime(appointment: Appointment): String {
    return try {
        val dateTime = java.time.Instant.ofEpochMilli(appointment.dateTime)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()

        val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
        val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a")

        "${dateTime.format(dateFormatter)} at ${dateTime.format(timeFormatter)}"
    } catch (_: Exception) {
        "Date not available"
    }
}