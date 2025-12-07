package com.example.capsule.ui.screens.doctor

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.isUpcoming
import com.example.capsule.ui.theme.WhiteSmoke
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


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

    LaunchedEffect(Unit) {
        viewModel.loadCurrentDoctorProfile()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(WhiteSmoke),
                title = {
                    Text(
                        text = stringResource(R.string.schedule),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF0A3140)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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
                EmptyDoctorScheduleState()
            } else {
                DoctorAppointmentsList(
                    appointments = appointments,
                    onPatientClick = onPatientClick,
                    onAppointmentClick = onViewAppointmentDetails,
                    onDeleteAppointment = { appointment ->
                        viewModel.deleteAppointment(appointment.id)
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
                text = "No upcoming appointments",
                color = Color.Gray,
                fontSize = 16.sp
            )
            Text(
                text = "Your schedule is clear for now",
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
    onDeleteAppointment: (Appointment) -> Unit
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
                onDeleteClick = { onDeleteAppointment(appointment) }
            )
        }
    }
}

@Composable
private fun DoctorAppointmentCard(
    appointment: Appointment,
    onPatientClick: () -> Unit,
    onAppointmentClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAppointmentClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Patient info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.clickable { onPatientClick() }
                ) {
                    Text(
                        text = appointment.patientName,
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
                            "Upcoming" -> Color.Green
                            "Completed" -> Color.Blue
                            "Cancelled" -> Color.Red
                            else -> Color.Gray
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Appointment time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "Time",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${appointment.timeSlot.start} - ${appointment.timeSlot.end}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Appointment date
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
                    text = Instant.ofEpochMilli(appointment.dateTime)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (appointment.isUpcoming) {
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Appointment") },
            text = {
                Text("Are you sure you want to remove this appointment with ${appointment.patientName}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Remove", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Keep")
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
