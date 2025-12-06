package com.example.capsule.ui.screens.prescription

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Medication
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.CapsuleTheme
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.formatDate
import com.google.firebase.auth.FirebaseAuth

// Enum to manage the screen state
enum class PrescriptionScreenState {
    SELECT_PATIENT,
    CREATE_PRESCRIPTION
}

@OptIn(ExperimentalMaterial3Api::class)
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
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (screenState == PrescriptionScreenState.SELECT_PATIENT)
                            "Select Patient"
                        else
                            "New Prescription",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (screenState) {
                            PrescriptionScreenState.SELECT_PATIENT -> onBack()
                            PrescriptionScreenState.CREATE_PRESCRIPTION -> {
                                screenState = PrescriptionScreenState.SELECT_PATIENT
                                viewModel.clearPrescription()
                            }
                        }
                    }) {
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
        Column(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

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
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A2A2A)
            )

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF6A6A6A))
                Text(
                    text = appointmentType,
                    fontSize = 14.sp,
                    color = Color(0xFF6A6A6A)
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF6A6A6A)
                )
                Text(
                    text = formatDate(date),
                    fontSize = 14.sp,
                    color = Color(0xFF6A6A6A)
                )
            }
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
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {

            // Patient Name
            Text(
                text = appointment.patientName,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E1E1E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Appointment Type chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFF2F4F7), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.Event,
                    contentDescription = null,
                    tint = Color(0xFF4A4A4A),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appointment.type,
                    fontSize = 14.sp,
                    color = Color(0xFF4A4A4A)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFF2F4F7), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF4A4A4A),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDate(appointment.dateTime),
                    fontSize = 14.sp,
                    color = Color(0xFF4A4A4A)
                )
            }
        }
    }
}

@Composable
fun MedicationEntry(
    medication: Medication,
    onUpdate: (Medication) -> Unit,
    onRemove: () -> Unit
) {
    var showValidationError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (medication.name.isNotBlank())
                        "Medication - ${medication.name}"
                    else
                        "Medication",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (showValidationError && medication.name.isBlank())
                        Color.Red
                    else
                        Color(0xFF2A2A2A)
                )

                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }

            // Validation
            if (showValidationError && medication.name.isBlank()) {
                Text(
                    text = "Drug name is required",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            // Name field
            OutlinedTextField(
                value = medication.name,
                onValueChange = {
                    onUpdate(medication.copy(name = it))
                    if (it.isNotBlank()) showValidationError = false
                },
                label = { Text("Drug Name *") },
                placeholder = { Text("Enter drug name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showValidationError && medication.name.isBlank()
            )

            Spacer(Modifier.height(14.dp))

            // Dosage + Frequency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = medication.dosage,
                    onValueChange = { onUpdate(medication.copy(dosage = it)) },
                    label = { Text("Dosage *") },
                    placeholder = { Text("e.g., 500mg") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = medication.frequency,
                    onValueChange = { onUpdate(medication.copy(frequency = it)) },
                    label = { Text("Frequency *") },
                    placeholder = { Text("e.g., Twice daily") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(14.dp))

            // Duration + Instructions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = medication.duration,
                    onValueChange = { onUpdate(medication.copy(duration = it)) },
                    label = { Text("Duration") },
                    placeholder = { Text("e.g., 7 days") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = medication.instructions,
                    onValueChange = { onUpdate(medication.copy(instructions = it)) },
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
