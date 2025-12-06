package com.example.capsule.ui.screens.prescription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.screens.doctor.DoctorViewModel
import com.example.capsule.ui.screens.patient.PatientViewModel
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
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
                title = {
                    Text(
                        "Prescriptions Details",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Cyan,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Teal
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(16.dp)
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (isDoctorView) "Patient: ${prescription.patientName}"
                    else "Doctor: ${prescription.doctorName}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A2A2A)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = formatDate(prescription.date),
                    fontSize = 14.sp,
                    color = Color(0xFF7D7D7D)
                )

                if (isDoctorView) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Doctor: ${prescription.doctorName}",
                        fontSize = 14.sp,
                        color = Color(0xFF7D7D7D)
                    )
                }
            }
        }

        // Section Title
        Text(
            text = "Medications",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF202020),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Medication Cards
        prescription.medications.values.forEachIndexed { index, medication ->
            MedicationDetailCard(medication = medication, index = index + 1)
        }

        // Notes section
        if (prescription.notes.isNotBlank()) {
            Text(
                text = "Notes",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202020),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = prescription.notes,
                    modifier = Modifier.padding(18.dp),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF3A3A3A)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {

            // Title Row
            Text(
                text = "$index. ${medication.name}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A2A2A)
            )

            Spacer(Modifier.height(14.dp))

            // Dosage + Frequency Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MedicationField(
                    label = "Dosage", value = medication.dosage,
                    modifier = Modifier.weight(1f)
                )
                MedicationField(
                    label = "Frequency",
                    value = medication.frequency,
                    modifier = Modifier.weight(1f)
                )
            }

            // Duration
            if (medication.duration.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                MedicationField(
                    label = "Duration",
                    value = medication.duration,
                    modifier = Modifier.weight(1f)
                )
            }

            // Instructions
            if (medication.instructions.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                MedicationField(
                    label = "Instructions",
                    value = medication.instructions,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MedicationField(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF8A8A8A)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF343434)
        )
    }
}
