package com.example.capsule.ui.screens.dashboards

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.ui.components.DashboardCard
import com.example.capsule.ui.components.UpcomingCard
import com.example.capsule.ui.screens.doctor.DoctorViewModel
import com.example.capsule.ui.screens.patient.PatientViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Green
import com.example.capsule.ui.theme.White
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.ProfileImage

@Composable
fun DoctorDashboardScreen(
    viewModel: DoctorViewModel = viewModel(),
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onPatientClick: (String) -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onMakePrescriptionClick: () -> Unit = {},
    onPrescriptionClick: () -> Unit = {}
) {
    val doctor = viewModel.doctor.value
    val appointments = viewModel.appointments.value
    val isLoading = viewModel.isLoading.value

    // Load doctor data when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadCurrentDoctorProfile()
    }

    // Show loading state
    if (doctor == null || isLoading) {
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
            }
        }
        return
    }

    // Get first 3 appointments
    val recentAppointments = appointments.take(3)

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                    ProfileImage(
                        base64Image = doctor.profileImageBase64,
                        defaultImageRes = R.drawable.doc_prof_unloaded,
                        modifier = Modifier.size(50.dp),
                        onImageClick = null
                    )

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
                IconButton(onClick = onSettingsClick) {
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
                DashboardCard(
                    title = stringResource(R.string.schedule),
                    icon = R.drawable.ic_calendar,
                    bgColor = Color(0xFFFFEAD8),
                    iconColor = Color(0xFFFF8728),
                    onClick = onScheduleClick,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Messages card
                DashboardCard(
                    title = stringResource(R.string.messages),
                    icon = R.drawable.ic_messages,
                    bgColor = Color(0xFFE4FBE4),
                    iconColor = Green,
                    onClick = onMessagesClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                onClick = onPrescriptionClick,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .padding(4.dp)
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
                            .background(Color(0xFFFBE4E4), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_prescription),
                            tint = Color(0xFFFF4141),
                            contentDescription = "Prescription"
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Prescription Sent",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Make Prescription Button
            Button(
                onClick = onMakePrescriptionClick,
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
            Text(
                text = stringResource(R.string.upcoming),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (recentAppointments.isEmpty()) {
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
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentAppointments) { appointment ->
                        UpcomingCard(
                            patientPic = appointment.patientProfileImage,
                            name = appointment.patientName,
                            appointmentType = appointment.type,
                            timeSlot = appointment.timeSlot,
                            date = appointment.dateTime,
                            onClick = { onPatientClick(appointment.patientId) },
                            showMoreIcon = false
                        )
                    }

                    // "View All" message if more appointments
                    if (appointments.size > 3) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "View ${appointments.size - 3} more appointments in Schedule",
                                    color = Blue,
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable { onScheduleClick() }
                                )
                            }
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
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