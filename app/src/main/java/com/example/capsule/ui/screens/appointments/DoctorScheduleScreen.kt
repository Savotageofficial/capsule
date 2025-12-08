package com.example.capsule.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.data.model.Appointment
import com.example.capsule.ui.components.FilterChipsRow
import com.example.capsule.ui.screens.doctor.DoctorViewModel
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.ProfileImage
import com.example.capsule.util.formatDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorScheduleScreen(
    viewModel: DoctorViewModel = viewModel(),
    onPatientClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onViewAppointmentDetails: (Appointment) -> Unit = {}
) {
    val appointments = viewModel.appointments.value
    val isLoading by viewModel.isLoading
    val filterState by viewModel.filterState

    LaunchedEffect(Unit) {
        viewModel.loadCurrentDoctorProfile()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.schedule),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
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
                EmptyDoctorScheduleState()
            } else {
                DoctorAppointmentsList(
                    appointments = appointments,
                    onPatientClick = onPatientClick,
                    onAppointmentClick = onViewAppointmentDetails,
                    onCancelAppointment = { appointment ->
                        viewModel.cancelAppointment(appointment.id)
                    },
                    onMarkAsCompleted = { appointment ->
                        viewModel.markAsCompleted(appointment.id)
                    }
                )
            }
        }
    }
}


@Composable
private fun EmptyDoctorScheduleState() {
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
                text = stringResource(R.string.your_schedule_is_clear_for_now),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun DoctorAppointmentsList(
    appointments: List<Appointment>,
    onPatientClick: (String) -> Unit,
    onAppointmentClick: (Appointment) -> Unit,
    onCancelAppointment: (Appointment) -> Unit,
    onMarkAsCompleted: (Appointment) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(appointments) { appointment ->
            DoctorAppointmentCard(
                appointment = appointment,
                onPatientClick = { onPatientClick(appointment.patientId) },
                onAppointmentClick = { onAppointmentClick(appointment) },
                onCancelClick = { onCancelAppointment(appointment) },
                onMarkAsCompleted = { onMarkAsCompleted(appointment) }
            )
        }
    }
}

@Composable
fun DoctorAppointmentCard(
    appointment: Appointment,
    onPatientClick: () -> Unit,
    onAppointmentClick: () -> Unit,
    onCancelClick: () -> Unit,
    onMarkAsCompleted: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onAppointmentClick() }
    ) {

        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // ---------------- LEFT SIDE ----------------
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Profile Image - NOW USING appointment.patientProfileImage
                ProfileImage(
                    base64Image = appointment.patientProfileImage, // Directly use from appointment
                    defaultImageRes = R.drawable.patient_profile,
                    modifier = Modifier
                        .size(52.dp)
                        .clickable { onPatientClick() },
                    onImageClick = { onPatientClick() }
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier
                    .clickable { onPatientClick() }
                ) {

                    // Patient Name
                    val shortPatientName = if (appointment.patientName.length > 10)
                        appointment.patientName.take(10) + "..."
                    else
                        appointment.patientName

                    Text(
                        text = shortPatientName,
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

                    Spacer(modifier = Modifier.height(4.dp))

                    // Time Slot
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF7D7D7D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = appointment.timeSlot.toDisplayString(),
                            fontSize = 14.sp,
                            color = Color(0xFF6D6D6D)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF7D7D7D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatDate(appointment.dateTime),
                            fontSize = 14.sp,
                            color = Color(0xFF7D7D7D)
                        )
                    }
                }
            }

            // ---------------- RIGHT SIDE ----------------

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // STATUS BADGE
                Box(
                    modifier = Modifier
                        .background(
                            color = when (appointment.status) {
                                "Upcoming" -> Color(0x3300FF00)
                                "Completed" -> Color(0x330000FF)
                                "Cancelled" -> Color(0x33FF0000)
                                else -> Color(0x33CCCCCC)
                            },
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = appointment.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (appointment.status) {
                            "Upcoming" -> Color(0xFF00AA00)
                            "Completed" -> Color(0xFF0044FF)
                            "Cancelled" -> Color.Red
                            else -> Color.Gray
                        }
                    )
                }

                // Action Buttons based on status
                when (appointment.status) {
                    "Upcoming" -> {
                        // Mark as Completed button
                        Text(
                            text = "Mark Complete",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00AA00),
                            modifier = Modifier.clickable { showCompleteDialog = true }
                        )

                        // Cancel button
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
                Text("Are you sure you want to cancel your appointment with ${appointment.patientName}?")
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

    // -------- Mark as Complete Dialog --------
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = { Text("Mark as Completed") },
            text = {
                Text("Mark appointment with ${appointment.patientName} as completed?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onMarkAsCompleted()
                        showCompleteDialog = false
                    }
                ) {
                    Text("Yes, Complete", color = Color(0xFF00AA00))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text("Not Yet")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DoctorScheduleScreenPreview() {
    MaterialTheme {
        DoctorScheduleScreen()
    }
}