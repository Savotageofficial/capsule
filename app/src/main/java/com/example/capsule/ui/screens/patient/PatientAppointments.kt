package com.example.capsule.ui.screens.patient

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.components.UpcomingCard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen(
    onBackClick: () -> Unit = {},
    viewModel: PatientProfileViewModel = viewModel()
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
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
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
                            details = "${dateTime.format(timeFormatter)} - ${appointment.type} • ${dateTime.format(dateFormatter)}",
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