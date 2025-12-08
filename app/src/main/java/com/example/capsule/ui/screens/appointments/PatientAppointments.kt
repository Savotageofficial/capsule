package com.example.capsule.ui.screens.appointments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.data.model.Appointment
import com.example.capsule.ui.components.FilterChipsRow
import com.example.capsule.ui.screens.patient.PatientViewModel
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.formatAppointmentDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen(
    onBackClick: () -> Unit = {},
    onDoctorClick: (String) -> Unit = {},
    viewModel: PatientViewModel = viewModel()
) {
    val appointments = viewModel.appointments.value
    val isLoading by viewModel.isLoading
    val filterState by viewModel.filterState

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
            // Add Filter Chips Row
            FilterChipsRow(
                selectedFilter = filterState,
                onFilterSelected = { filter ->
                    viewModel.setFilter(filter)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    onDoctorClick = onDoctorClick,
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
                text = stringResource(R.string.book_your_an_appointment_with_a_doctor_now),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun AppointmentsList(
    appointments: List<Appointment>,
    onDoctorClick: (String) -> Unit,
    onCancelAppointment: (Appointment) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appointment = appointment,
                onDoctorClick = { onDoctorClick(appointment.doctorId) },
                onCancelClick = { onCancelAppointment(appointment) }
            )
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: Appointment,
    onDoctorClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onDoctorClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ---------------- Left Side: Profile + Info ----------------
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Doctor Picture
                Image(
                    painter = painterResource(R.drawable.doc_prof_unloaded),
                    contentDescription = "Doctor Profile",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFEFEF), CircleShape)
                        .clickable { onDoctorClick() },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.clickable { onDoctorClick() }) {
                    // Doctor Name
                    Text(
                        text = "Dr. ${appointment.doctorName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1C)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Appointment Type
                    Text(
                        text = appointment.type,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Time Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF6D6D6D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = appointment.timeSlot.toDisplayString(),
                            fontSize = 14.sp,
                            color = Color(0xFF6D6D6D)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Date Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF7D7D7D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatAppointmentDateTime(appointment),
                            fontSize = 14.sp,
                            color = Color(0xFF7D7D7D)
                        )
                    }
                }
            }

            // ---------------- Right Side: Status + Action ----------------
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status badge
                Box(
                    modifier = Modifier
                        .background(
                            color = when (appointment.status) {
                                "Upcoming" -> Color(0x3328B463)
                                "Completed" -> Color(0x334195F4)
                                "Cancelled" -> Color(0x33E53935)
                                else -> Color(0x33CCCCCC)
                            },
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = appointment.status,
                        color = when (appointment.status) {
                            "Upcoming" -> Color(0xFF2E7D32)
                            "Completed" -> Color(0xFF1E88E5)
                            "Cancelled" -> Color(0xFFD32F2F)
                            else -> Color.Gray
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Action buttons based on status
                when (appointment.status) {
                    "Upcoming" -> {
                        // Cancel button for upcoming appointments
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Red,
                            modifier = Modifier.clickable { showCancelDialog = true }
                        )
                    }
                }
            }
        }
    }

    // -------- Cancel Dialog --------
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Appointment") },
            text = {
                Text("Are you sure you want to cancel your appointment with Dr. ${appointment.doctorName}?")
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
