package com.example.capsule.ui.screens.prescription

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.capsule.ui.screens.patient.PatientViewModel
import com.example.capsule.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPrescriptionScreen(
    prescriptionId: String,
    isDoctorView: Boolean = false,
    onBackClick: () -> Unit
) {
    val doctorVM: DoctorViewModel? = if (isDoctorView) viewModel() else null
    val patientVM: PatientViewModel? = if (!isDoctorView) viewModel() else null

    val prescription = if (isDoctorView) {
        doctorVM?.selectedPrescription?.value
    } else {
        patientVM?.selectedPrescription?.value
    }

    val isLoading = if (isDoctorView) {
        doctorVM?.isLoading?.value ?: false
    } else {
        patientVM?.isLoading?.value ?: false
    }

    // Load prescription
    LaunchedEffect(prescriptionId) {
        if (isDoctorView) {
            doctorVM?.loadPrescriptionById(prescriptionId)
        } else {
            patientVM?.loadPrescriptionById(prescriptionId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Prescription Details", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else if (prescription == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Prescription not found", color = Color.Gray)
                }
            } else {
                PrescriptionDetailContent(prescription, isDoctorView)
            }
        }
    }
}

@Composable
fun PrescriptionDetailContent(
    prescription: com.example.capsule.data.model.Prescription,
    isDoctorView: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isDoctorView) "Patient: ${prescription.patientName}"
                    else "Doctor: ${prescription.doctorName}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Date: ${formatDate(prescription.date)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                if (isDoctorView) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Doctor: ${prescription.doctorName}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Medications Section
        Text(
            text = "Medications",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        prescription.medications.values.forEachIndexed { index, medication ->
            MedicationDetailCard(
                medication = medication,
                index = index + 1
            )
        }

        // Notes Section
        if (prescription.notes.isNotBlank()) {
            Text(
                text = "Notes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = prescription.notes,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun MedicationDetailCard(
    medication: com.example.capsule.data.model.Medication,
    index: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$index. ${medication.name}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dosage", fontSize = 12.sp, color = Color.Gray)
                    Text(medication.dosage, fontSize = 14.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Frequency", fontSize = 12.sp, color = Color.Gray)
                    Text(medication.frequency, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            if (medication.duration.isNotBlank()) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Duration", fontSize = 12.sp, color = Color.Gray)
                        Text(medication.duration, fontSize = 14.sp)
                    }
                }
            }

            if (medication.instructions.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Column {
                    Text("Instructions", fontSize = 12.sp, color = Color.Gray)
                    Text(medication.instructions, fontSize = 14.sp)
                }
            }
        }
    }
}