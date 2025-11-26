package com.example.capsule.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.data.repository.ProfileRepository
import com.example.capsule.util.convertDateTimeToMillis
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingBottomSheet(
    doctor: Doctor,
    onConfirm: (Long, TimeSlot, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var appointmentType by remember { mutableStateOf("In-Person") }
    var availableSlots by remember { mutableStateOf<List<TimeSlot>>(emptyList()) }
    var isLoadingSlots by remember { mutableStateOf(false) }

    val profileRepository = ProfileRepository.getInstance()

    // Load available slots when date changes
    LaunchedEffect(selectedDate) {
        isLoadingSlots = true
        selectedSlot = null

        val timestamp = convertDateTimeToMillis(selectedDate, "00:00")
        profileRepository.getAvailableTimeSlots(
            doctorId = doctor.id,
            selectedDate = timestamp,
            doctorAvailability = doctor.availability
        ) { slots ->
            availableSlots = slots
            isLoadingSlots = false
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Text("Book with Dr. ${doctor.name}", style = MaterialTheme.typography.headlineSmall)
            Text(doctor.specialty, color = Color.Gray)

            Spacer(Modifier.height(20.dp))

            // Simple Type Selection
            Text("Appointment Type", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("In-Person", "Video Call").forEach { type ->
                    val isSelected = appointmentType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { appointmentType = type },
                        label = { Text(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Simple Date Selection
            Text("Select Date", style = MaterialTheme.typography.titleMedium)
            CalendarView(
                selectedDate = selectedDate,
                onDateSelected = {
                    selectedDate = it
                    selectedSlot = null
                }
            )

            Spacer(Modifier.height(20.dp))

            // Time Slots
            Text("Available Times", style = MaterialTheme.typography.titleMedium)

            if (isLoadingSlots) {
                Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (availableSlots.isEmpty()) {
                Text(
                    "No slots available on ${selectedDate.dayOfWeek}",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableSlots) { slot ->
                        val isSelected = selectedSlot == slot
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSlot = slot },
                            label = { Text("${slot.start} - ${slot.end}") }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Simple Confirm Button
            Button(
                onClick = {
                    val timestamp = convertDateTimeToMillis(selectedDate, selectedSlot!!.start)
                    onConfirm(timestamp, selectedSlot!!, appointmentType)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = selectedSlot != null && !isLoadingSlots
            ) {
                Text("Book Appointment", fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BookingBottomSheetPreview() {
    // Create a mock doctor for preview
    val mockDoctor = Doctor(
        id = "doctor_123",
        name = "John Smith",
        specialty = "Cardiologist",
        bio = "Experienced cardiologist with 10+ years of practice",
        rating = 4.8,
        reviewsCount = 125,
        experience = "10 years",
        clinicName = "Heart Care Center",
        clinicAddress = "123 Medical Drive, City",
        availability = mapOf(
            "Monday" to listOf(
                TimeSlot("09:00", "12:00"),
                TimeSlot("14:00", "17:00")
            ),
            "Tuesday" to listOf(
                TimeSlot("10:00", "13:00"),
                TimeSlot("15:00", "18:00")
            ),
            "Wednesday" to listOf(
                TimeSlot("08:00", "12:00")
            )
        )
    )

    MaterialTheme {
        // You need to wrap it in a ModalBottomSheet for the preview to work properly
        ModalBottomSheet(onDismissRequest = {}) {
            BookingBottomSheet(
                doctor = mockDoctor,
                onConfirm = { timestamp, slot, type ->
                    // Preview callback - does nothing
                },
                onDismiss = {
                    // Preview callback - does nothing
                }
            )
        }
    }
}