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
import com.example.capsule.R
import com.example.capsule.ui.components.UpcomingCard
import com.example.capsule.ui.screens.viewmodels.DoctorProfileViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Red
import com.example.capsule.ui.theme.White

@Composable
fun DoctorDashboardScreen(
    viewModel: DoctorProfileViewModel = DoctorProfileViewModel(),
    onProfileClick: () -> Unit = {},
    onPatientClick: (String) -> Unit = {}
) {
    val doctor = viewModel.doctor.value
    val upcomingAppointments = viewModel.upcomingAppointments

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
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
                    doctor.profileImageRes?.let {
                        Image(
                            painter = painterResource(id = it),
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
                        contentDescription = "Settings"
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
                    onClick = { /* open doctor Schedule */ },
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
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.schedule),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Messages card
                Card(
                    onClick = { /* open chat screen */ },
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
                                contentDescription = null
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_unread_message),
                                tint = Red,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.TopEnd)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.messages),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(
                onClick = { /* Go to make prescription screen */ },
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prescription),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.make_a_prescription), fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Upcoming Section
            Text(stringResource(R.string.upcoming), fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(upcomingAppointments) { appointment ->
                    UpcomingCard(
                        name = appointment.patientName,
                        details = "${appointment.time} - ${appointment.type}",
                        onClick = { onPatientClick(appointment.patientName) }, // navigate to profile
                        showMoreIcon = false
                    )
                }

                if (upcomingAppointments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_upcoming_appointments),
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
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
        DoctorDashboardScreen()
    }
}
