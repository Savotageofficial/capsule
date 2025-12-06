package com.example.capsule.ui.screens.prescription

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.screens.doctor.DoctorViewModel
import com.example.capsule.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorPrescriptionsScreen(
    viewModel: DoctorViewModel = viewModel(),
    onBackClick: () -> Unit,
    onViewPrescription: (String) -> Unit,
    onNewPrescription: () -> Unit
) {
    val prescriptions = viewModel.prescriptions.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(Unit) {
        viewModel.loadCurrentDoctorProfile()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Prescriptions", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewPrescription,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "New Prescription")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
                Log.d("trace", "loading")
            } else if (prescriptions.isEmpty()) {
                Log.d("trace", "empty")

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("No prescriptions yet", color = Color.Gray, fontSize = 16.sp)
                        Text(
                            "Create your first prescription for a patient",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Log.d("trace", "list")

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(prescriptions) { prescription ->
                        DoctorPrescriptionCard(
                            prescription = prescription,
                            onClick = { onViewPrescription(prescription.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorPrescriptionCard(
    prescription: com.example.capsule.data.model.Prescription,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "For: ${prescription.patientName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = formatDate(prescription.date),
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(8.dp))

            val firstMed = prescription.medications.values.firstOrNull()
            if (firstMed != null) {
                Text(
                    text = "${firstMed.name} - ${firstMed.dosage}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(Modifier.height(4.dp))

            if (prescription.medications.size > 1) {
                Text(
                    text = "+ ${prescription.medications.size - 1} more medications",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}