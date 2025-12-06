package com.example.capsule.ui.screens.prescription

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Medication
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.CapsuleTheme
import com.example.capsule.util.formatDate
import com.google.firebase.auth.FirebaseAuth

// Enum to manage the screen state
enum class PrescriptionScreenState {
    SELECT_PATIENT,
    CREATE_PRESCRIPTION
}

@Composable
fun MakeNewPrescriptionScreen(
    viewModel: PrescriptionViewModel = viewModel(),
    onBack: () -> Unit,
    onSavePrescription: () -> Unit,
) {
    val prescription by viewModel.prescription.collectAsState()
    val medications by viewModel.medications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val selectedPatient by viewModel.selectedPatient.collectAsState()

    var screenState by remember { mutableStateOf(PrescriptionScreenState.SELECT_PATIENT) }

    val doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadDoctorAppointments()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        when (screenState) {
                            PrescriptionScreenState.SELECT_PATIENT -> onBack()
                            PrescriptionScreenState.CREATE_PRESCRIPTION -> {
                                screenState = PrescriptionScreenState.SELECT_PATIENT
                                viewModel.clearPrescription()
                            }
                        }
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (screenState == PrescriptionScreenState.SELECT_PATIENT)
                    "Select Patient"
                else
                    "New Prescription",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(20.dp))

        when (screenState) {

            // ----------------------------
            // 1. SELECT PATIENT SCREEN
            // ----------------------------
            PrescriptionScreenState.SELECT_PATIENT -> {
                SelectPatientSection(
                    appointments = appointments,
                    isLoading = isLoading,
                    onPatientSelected = { appointment ->
                        viewModel.selectPatient(appointment)
                        screenState = PrescriptionScreenState.CREATE_PRESCRIPTION
                    }
                )
            }

            // ----------------------------
            // 2. CREATE PRESCRIPTION SCREEN
            // ----------------------------
            PrescriptionScreenState.CREATE_PRESCRIPTION -> {
                selectedPatient?.let { appointment ->
                    CreatePrescriptionSection(
                        appointment = appointment,
                        prescription = prescription,
                        medications = medications,
                        isLoading = isLoading,
                        onAddMedication = { viewModel.addMedication() },
                        onUpdateMedication = { index, med ->
                            viewModel.updateMedication(index, med)
                        },
                        onRemoveMedication = { index ->
                            viewModel.removeMedication(index)
                        },
                        onUpdateNotes = { notes ->
                            viewModel.updateNotes(notes)
                        },
                        onSavePrescription = {

                            viewModel.savePrescription(
                                patientId = appointment.patientId,
                                patientName = appointment.patientName,
                                doctorId = doctorId,
                                doctorName = appointment.doctorName,
                                notes = prescription.notes,
                                medications = medications.associateBy { it.name }
                            ) { success ->
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Prescription Sent Successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onSavePrescription()
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun SelectPatientSection(
    appointments: List<Appointment>,
    isLoading: Boolean,
    onPatientSelected: (Appointment) -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (appointments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No appointments found", color = Color.Gray, fontSize = 16.sp)
        }
        return
    }

    Text("Select a patient:", fontSize = 16.sp, color = Color.Gray)
    Spacer(Modifier.height(16.dp))

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appointments.forEach { appointment ->
            PatientAppointmentCard(
                appointment = appointment,
                onClick = { onPatientSelected(appointment) }
            )
        }
    }
}

@Composable
fun CreatePrescriptionSection(
    appointment: Appointment,
    prescription: com.example.capsule.data.model.Prescription,
    medications: List<Medication>,
    isLoading: Boolean,
    onAddMedication: () -> Unit,
    onUpdateMedication: (Int, Medication) -> Unit,
    onRemoveMedication: (Int) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onSavePrescription: () -> Unit
) {
    val context = LocalContext.current

    PatientInfoCard(
        name = appointment.patientName,
        appointmentType = appointment.type,
        date = appointment.dateTime
    )

    Spacer(Modifier.height(24.dp))

    // -------------------------
    // Medications Section
    // -------------------------
    Text("Medications", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        medications.forEachIndexed { index, med ->
            MedicationEntry(
                medication = med,
                onUpdate = { updated -> onUpdateMedication(index, updated) },
                onRemove = { onRemoveMedication(index) }
            )
        }
    }

    OutlinedButton(
        onClick = onAddMedication,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Add Medication")
    }

    Spacer(Modifier.height(24.dp))

    // -------------------------
    // Notes
    // -------------------------
    Text("Notes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = prescription.notes,
        onValueChange = onUpdateNotes,
        modifier = Modifier.fillMaxWidth(),
        maxLines = 4,
        placeholder = { Text("Additional notes...") }
    )

    Spacer(Modifier.height(24.dp))

    // -------------------------
    // Save Button
    // -------------------------
    Button(
        onClick = {
            val invalid = medications.any {
                it.name.isBlank() || it.dosage.isBlank() || it.frequency.isBlank()
            }

            if (medications.isEmpty()) {
                Toast.makeText(context, "Add at least one medication", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (invalid) {
                Toast.makeText(context, "Fill all medication fields", Toast.LENGTH_SHORT).show()
                return@Button
            }

            onSavePrescription()
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(Blue)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text("Save Prescription", color = Color.White)
        }
    }
}


@Composable
fun PatientInfoCard(
    name: String,
    appointmentType: String,
    date: Long,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Appointment: $appointmentType", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text("Date: ${formatDate(date)}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PatientAppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = appointment.patientName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Appointment: ${appointment.type}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: ${formatDate(appointment.dateTime)}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// Keep the existing MedicationEntry composable as is
@Composable
fun MedicationEntry(
    medication: Medication,
    onUpdate: (Medication) -> Unit,
    onRemove: () -> Unit
) {
    var showValidationError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medication ${if (medication.name.isNotEmpty()) "- ${medication.name}" else ""}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (showValidationError && medication.name.isBlank()) Color.Red else Color.Unspecified
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.Red
                    )
                }
            }

            if (showValidationError && medication.name.isBlank()) {
                Text(
                    text = "Drug name is required",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Medication fields
            OutlinedTextField(
                value = medication.name,
                onValueChange = {
                    if (it.length <= 100) {
                        onUpdate(medication.copy(name = it))
                        if (showValidationError && it.isNotBlank()) {
                            showValidationError = false
                        }
                    }
                },
                label = { Text("Drug Name *") },
                placeholder = { Text("Enter drug name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showValidationError && medication.name.isBlank(),
//                Colors = TextFieldDefaults.outlinedTextFieldColors(
//                    focusedBorderColor = MaterialTheme.colorScheme.primary,
//                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
//                )
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = medication.dosage,
                    onValueChange = {
                        if (it.length <= 50) onUpdate(medication.copy(dosage = it))
                    },
                    label = { Text("Dosage *") },
                    placeholder = { Text("e.g., 500mg") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = showValidationError && medication.dosage.isBlank()
                )

                OutlinedTextField(
                    value = medication.frequency,
                    onValueChange = {
                        if (it.length <= 50) onUpdate(medication.copy(frequency = it))
                    },
                    label = { Text("Frequency *") },
                    placeholder = { Text("e.g., Twice daily") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = showValidationError && medication.frequency.isBlank()
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = medication.duration,
                    onValueChange = {
                        if (it.length <= 50) onUpdate(medication.copy(duration = it))
                    },
                    label = { Text("Duration") },
                    placeholder = { Text("e.g., 7 days") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = medication.instructions,
                    onValueChange = {
                        if (it.length <= 200) onUpdate(medication.copy(instructions = it))
                    },
                    label = { Text("Instructions") },
                    placeholder = { Text("Special instructions") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
    }
}

// Update the preview to not require patientAppointment parameter
@Preview(showBackground = true)
@Composable
fun MakeNewPrescriptionScreenPreview() {
    CapsuleTheme {
        MakeNewPrescriptionScreen(
            onBack = {},
            onSavePrescription = {},
        )
    }
}
