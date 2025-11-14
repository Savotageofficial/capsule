package com.example.capsule.ui.screens.dashboards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.data.model.Appointment
import com.example.capsule.ui.components.UpcomingCard
import com.example.capsule.ui.screens.viewmodels.DoctorProfileViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Red
import com.example.capsule.ui.theme.White

@Composable
fun DoctorDashboardScreen(
    viewModel: DoctorProfileViewModel = viewModel(),
    onProfileClick: () -> Unit = {},
    onPatientClick: (String) -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onPrescriptionClick: () -> Unit = {}
) {
    val doctor = viewModel.doctor.value

    // Load doctor data when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadCurrentDoctorProfile()
    }

    // Dummy appointments data - replace with real data from ViewModel later
    val upcomingAppointments = remember {
        listOf(
            Appointment(
                id = "1",
                patientName = "Sarah Johnson",
                time = "10:00 AM",
                type = "In-Person",
                status = "Upcoming"
            ),
            Appointment(
                id = "2",
                patientName = "Mike Wilson",
                time = "2:30 PM",
                type = "Video Call",
                status = "Upcoming"
            ),
            Appointment(
                id = "3",
                patientName = "Emma Davis",
                time = "4:15 PM",
                type = "In-Person",
                status = "Upcoming"
            )
        )
    }

    // Show loading state
    if (doctor == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = Blue)
                Text(
                    text = "Loading your dashboard...",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
        return
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .background(White)
        ) {

            // Top Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .padding(horizontal = 5.dp)
                    .clickable { onProfileClick() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    doctor.profileImageRes?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = "Doctor Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                    } ?: run {
                        // Fallback image if profileImageRes is null
                        Image(
                            painter = painterResource(id = R.drawable.doc_prof_unloaded),
                            contentDescription = "Doctor Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.welcome_back),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = doctor.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
                IconButton(onClick = { /* navigate to settings */ }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                // Schedule card
                Card(
                    onClick = onScheduleClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFFEAD8), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_calendar),
                                tint = Color(0xFFFF8728),
                                contentDescription = "Schedule"
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.schedule),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Messages card
                Card(
                    onClick = onMessagesClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE4FBE4), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_messages),
                                tint = Color(0xFF07B607),
                                contentDescription = "Messages"
                            )
                            // Unread message indicator
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Red, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.messages),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Make Prescription Button
            Button(
                onClick = onPrescriptionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prescription),
                    contentDescription = "Prescription",
                    tint = White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.make_a_prescription),
                    fontSize = 18.sp,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Upcoming Section
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.upcoming),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "See All",
                    color = Blue,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onScheduleClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (upcomingAppointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = "No appointments",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.no_upcoming_appointments),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Add time slots to get appointments",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(upcomingAppointments) { appointment ->
                        UpcomingCard(
                            name = appointment.patientName,
                            details = "${appointment.time} - ${appointment.type}",
                            onClick = { onPatientClick(appointment.patientName) },
                            showMoreIcon = false
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DoctorDashboardScreenPreview() {
    MaterialTheme {
        DoctorDashboardScreen(
            onProfileClick = {},
            onPatientClick = {},
            onScheduleClick = {},
            onMessagesClick = {},
            onPrescriptionClick = {}
        )
    }
}