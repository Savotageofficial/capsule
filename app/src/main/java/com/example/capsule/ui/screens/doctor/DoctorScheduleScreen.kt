package com.example.capsule.ui.screens.doctor

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.data.model.Appointment
import com.example.capsule.ui.theme.WhiteSmoke
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
fun DoctorAppointmentCard(
    appointment: Appointment,
    onPatientClick: () -> Unit,
    onAppointmentClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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

                // Profile Image
                Image(
                    painter = painterResource(R.drawable.patient_profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFEFEF), CircleShape)
                        .clickable { onPatientClick() },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.clickable { onPatientClick() }) {

                    // Patient Name
                    Text(
                        text = appointment.patientName,
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

            Column(horizontalAlignment = Alignment.End) {

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

                Spacer(modifier = Modifier.height(6.dp))

                // THREE DOTS MENU
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = "",
                        tint = Color(0xFF505050)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        onClick = {
                            menuExpanded = false
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // ------------ DELETE CONFIRMATION DIALOG ------------
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
