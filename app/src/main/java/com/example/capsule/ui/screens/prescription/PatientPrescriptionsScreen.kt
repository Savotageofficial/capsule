package com.example.capsule.ui.screens.prescription

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.capsule.ui.screens.patient.PatientViewModel
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientPrescriptionsScreen(
    viewModel: PatientViewModel = viewModel(),
    onBackClick: () -> Unit,
    onViewPrescription: (String) -> Unit
) {
    val prescriptions = viewModel.prescriptions.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(Unit) {
        viewModel.loadCurrentPatientProfile()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Prescriptions",
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
            } else if (prescriptions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No prescriptions found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(prescriptions) { prescription ->
                        PatientPrescriptionCard(
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
fun PatientPrescriptionCard(
    prescription: com.example.capsule.data.model.Prescription,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(18.dp)
        ) {

            // Header Row (Doctor name + date)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Dr. ${prescription.doctorName}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A2A2A)
                )

                Text(
                    text = formatDate(prescription.date),
                    fontSize = 13.sp,
                    color = Color(0xFF7D7D7D)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Medication preview chip
            val firstMed = prescription.medications.values.firstOrNull()
            if (firstMed != null) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFF5F6FA),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${firstMed.name} • ${firstMed.dosage}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A4A4A)
                    )
                }
            }

            if (prescription.medications.size > 1) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "+ ${prescription.medications.size - 1} more medications",
                    fontSize = 12.sp,
                    color = Color(0xFF909090)
                )
            }
        }
    }
}
