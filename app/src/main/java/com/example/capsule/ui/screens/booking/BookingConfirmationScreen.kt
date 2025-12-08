package com.example.capsule.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.ui.screens.doctor.DoctorViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Green
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.formatAppointmentDateTime
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(
    doctorId: String,
    selectedDate: Long,
    selectedSlot: TimeSlot,
    appointmentType: String,
    onBackToHome: () -> Unit,
    doctorViewModel: DoctorViewModel = viewModel()
) {
    val doctor by doctorViewModel.doctor

    LaunchedEffect(doctorId) {
        if (doctorId.isNotEmpty()) {
            doctorViewModel.loadDoctorProfileById(doctorId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.booking_confirmation),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Teal
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(16.dp)
            ) {
                Button(
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Home")
                }
            }
        }
    ) { padding ->
        if (doctor == null) {
            Box(
                modifier = Modifier
                    .background(WhiteSmoke)
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .background(WhiteSmoke)
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // Success Icon
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Green,
                    modifier = Modifier.size(90.dp)
                )

                Text(
                    text = "Appointment Booked Successfully!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Green
                )

                // Appointment Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // Doctor Info
                        DetailRow(
                            icon = Icons.Default.Person,
                            title = "Doctor",
                            line1 = "Dr. ${doctor!!.name}",
                            line2 = doctor!!.specialty
                        )

                        // Date & Time
                        DetailRow(
                            icon = Icons.Default.Schedule,
                            title = "Date & Time",
                            line1 = formatAppointmentDateTime(selectedDate, selectedSlot)
                        )

                        // Price
                        DetailRow(
                            painter = painterResource(R.drawable.ic_price),
                            title = "Session Fee",
                            line1 = doctor!!.formattedSessionPrice,
                            tint = Blue
                        )

                        // Appointment Type
                        DetailRow(
                            painter = painterResource(R.drawable.ic_appointment_type),
                            title = "Appointment Type",
                            line1 = appointmentType
                        )

                        if (appointmentType == "In-Person") {
                            DetailRow(
                                painter = painterResource(R.drawable.ic_location),
                                title = "Location",
                                line1 = doctor!!.clinicName,
                                line2 = doctor!!.clinicAddress
                            )
                        }
                    }
                }

                // Important Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Important Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = when (appointmentType) {
                                "In-Person" -> "• Arrive 15 minutes early\n• Bring ID & insurance\n• Cancel 24 hours early if needed"
                                "Chat" -> "• Chat link arrives 15 minutes before\n• Ensure stable internet\n• Stay in a quiet place"
                                else -> "• Follow instructions sent via email"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector? = null,
    painter: Painter? = null,
    title: String,
    line1: String,
    line2: String? = null,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        } else if (painter != null) {
            Icon(painter, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))

        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(line1, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            if (line2 != null) {
                Text(line2, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}
