package com.example.capsule.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.util.convertDateTimeToMillis
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingBottomSheet(
    doctor: Doctor,
    onConfirm: (Long, TimeSlot, String) -> Unit, // timestamp + slot + type
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var appointmentType by remember { mutableStateOf("In-Person") }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Book Appointment",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            // Doctor Info
            Text(doctor.name, style = MaterialTheme.typography.titleMedium)
            Text(doctor.specialty, color = Color.Gray)

            Spacer(Modifier.height(20.dp))

            // Appointment Type
            Text("Appointment Type", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("In-Person", "Video Call").forEach { type ->
                    val isSelected = appointmentType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) Color(0xFF2CCCD3) else Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { appointmentType = type }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) Color(0xFF2CCCD3) else Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Calendar
            Text("Select Date", style = MaterialTheme.typography.titleMedium)
            CalendarView(
                selectedDate = selectedDate,
                onDateSelected = {
                    selectedDate = it
                    selectedSlot = null
                }
            )

            Spacer(Modifier.height(20.dp))

            // Available Time Slots
            Text("Available Time", style = MaterialTheme.typography.titleMedium)

            val selectedDay = selectedDate.dayOfWeek.name
                .substring(0, 3)
                .lowercase()
                .replaceFirstChar { it.uppercase() }
            val slots = doctor.availability[selectedDay] ?: emptyList()

            if (slots.isEmpty()) {
                Text("No available slots for $selectedDay", color = Color.Gray)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(slots) { slot ->
                        val isSelected = selectedSlot == slot
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) Color(0xFF2CCCD3) else Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedSlot = slot }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "${slot.start} - ${slot.end}",
                                color = if (isSelected) Color(0xFF2CCCD3) else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Confirm Button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                enabled = selectedSlot != null,
                onClick = {
                    val timestamp = convertDateTimeToMillis(selectedDate, selectedSlot!!.start)
                    onConfirm(timestamp, selectedSlot!!, appointmentType)
                }
            ) {
                Text("Confirm Booking", fontSize = 16.sp)
            }
        }
    }
}