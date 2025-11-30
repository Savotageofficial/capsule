package com.example.capsule.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.components.UpcomingCard
import com.example.capsule.ui.theme.WhiteSmoke
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen(
    onBackClick: () -> Unit = {},
    viewModel: PatientViewModel = viewModel()
) {
    val appointments = viewModel.appointments.value

    LaunchedEffect(Unit) {
        viewModel.loadPatientAppointments()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Appointments",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color(0xFF0CA7BA),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { onBackClick() }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Color(0xFF0A3140)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(16.dp)
                .background(WhiteSmoke)
                .fillMaxSize()
        ) {
            if (appointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming appointments",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(appointments) { appointment ->
                        val dateTime = Instant.ofEpochMilli(appointment.dateTime)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()

                        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

                        UpcomingCard(
                            name = "Dr. ${appointment.doctorName}",
                            details = "${dateTime.format(timeFormatter)} - ${appointment.type} • ${
                                dateTime.format(
                                    dateFormatter
                                )
                            }",
                            showMoreIcon = true,
                            onClick = { /* View appointment details */ },
                            onDeleteClick = {
                                viewModel.cancelAppointment(appointment.id)
                            }
                        )
                    }
                }
            }
        }
    }
}