package com.example.capsule.ui.screens.features

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.components.UpcomingCard
import com.example.capsule.ui.screens.viewmodels.DoctorProfileViewModel
import com.example.capsule.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorScheduleScreen(
    viewModel: DoctorProfileViewModel = viewModel(),
    onPatientClick: (String) -> Unit = {},
    onAddSlotClick: () -> Unit = {},
    onBackClick: () -> Unit = {}

) {
    val upcomingAppointments = viewModel.upcomingAppointments

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(com.example.capsule.R.string.schedule),
                        style = MaterialTheme.typography.titleLarge
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSlotClick,
                containerColor = Blue
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Slot",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (upcomingAppointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming appointments",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(upcomingAppointments) { appointment ->
                        UpcomingCard(
                            name = appointment.patientName,
                            details = "${appointment.time} - ${appointment.type}",
                            onClick = { onPatientClick(appointment.patientName) },
                            showMoreIcon = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DoctorScheduleScreenPreview() {
    MaterialTheme {
        DoctorScheduleScreen()
    }
}
