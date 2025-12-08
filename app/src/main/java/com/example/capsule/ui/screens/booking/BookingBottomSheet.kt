package com.example.capsule.ui.screens.booking

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.util.ProfileImage
import com.example.capsule.ui.components.DatePickerDialog as CapsuleDatePickerDialog
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingBottomSheet(
    doctor: Doctor,
    patient: Patient?,
    onBookingConfirmed: (Long, TimeSlot, String) -> Unit,
    onDismiss: () -> Unit,
    bookingViewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current

    // Observe state
    val selectedDate by bookingViewModel.selectedDate
    val allSlots by bookingViewModel.allSlots
    val selectedSlot by bookingViewModel.selectedSlot
    val appointmentType by bookingViewModel.appointmentType
    val isLoading by bookingViewModel.isLoading
    val isBooking by bookingViewModel.isBooking
    val showConfirmation by bookingViewModel.showConfirmation
    val showDatePicker by bookingViewModel.showDatePicker

    // Load slots when doctor or date changes
    LaunchedEffect(doctor.id, selectedDate) {
        bookingViewModel.loadSlots(doctor)
    }

    // Date picker
    if (showDatePicker) {
        CapsuleDatePickerDialog(
            onDismissRequest = { bookingViewModel.showDatePicker.value = false },
            onDateSelected = { millis ->
                val date = Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                bookingViewModel.selectedDate.value = date
                bookingViewModel.selectedSlot.value = null
                bookingViewModel.loadSlots(doctor)
                bookingViewModel.showDatePicker.value = false
            }
        )
    }

    // Confirmation dialog
    if (showConfirmation && selectedSlot != null) {
        AlertDialog(
            onDismissRequest = { bookingViewModel.showConfirmation.value = false },
            title = { Text("Confirm Booking") },
            text = {
                Column {
                    Text("Doctor: Dr. ${doctor.name}")
                    Text("Date: ${selectedDate.dayOfWeek}, ${selectedDate.month} ${selectedDate.dayOfMonth}")
                    Text("Time: ${selectedSlot!!.start} - ${selectedSlot!!.end}")
                    Text("Type: $appointmentType")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        bookingViewModel.bookAppointment(
                            doctor = doctor,
                            patient = patient,
                            onSuccess = { timestamp, slot, type ->
                                onBookingConfirmed(timestamp, slot, type)
                            },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled = !isBooking
                ) {
                    if (isBooking) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingViewModel.showConfirmation.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Main content
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            // Header
            BookingSheetHeader(doctor)

            Spacer(Modifier.height(20.dp))

            // Appointment type
            Text("Appointment Type", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(listOf("In-Person", "Chat")) { type ->
                    FilterChip(
                        selected = appointmentType == type,
                        onClick = { bookingViewModel.appointmentType.value = type },
                        label = { Text(type) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Date selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Selected Date", fontWeight = FontWeight.SemiBold)
                    Text("${selectedDate.dayOfWeek}, ${selectedDate.month} ${selectedDate.dayOfMonth}")
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { bookingViewModel.showDatePicker.value = true }) {
                    Text("Change date")
                }
            }

            Spacer(Modifier.height(20.dp))

            // Time slot section - Simplified for single slot
            Text("Available Time", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            } else if (allSlots.isEmpty()) {
                Text("No availability for this day", color = Color.Gray)
            } else {
                // Since there's only one slot per day, display it prominently
                allSlots.firstOrNull()?.let { slot ->
                    val isSelected = selectedSlot == slot

                    SingleTimeSlotDisplay(
                        slot = slot,
                        isSelected = isSelected,
                        onSlotClick = {
                            bookingViewModel.selectedSlot.value = slot // Always clickable
                        }
                    )

                    if (isSelected) {
                        Text(
                            "Time slot selected - Ready to book",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Book button
            Button(
                onClick = {
                    if (selectedSlot == null) {
                        Toast.makeText(context, "Please select a time slot", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        bookingViewModel.showConfirmation.value = true
                    }
                },
                enabled = selectedSlot != null && !isBooking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                if (isBooking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Book Appointment")
                }
            }
        }
    }
}

@Composable
private fun SingleTimeSlotDisplay(
    slot: TimeSlot,
    isSelected: Boolean,
    onSlotClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onSlotClick) // Always enabled
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Time Slot",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "${slot.start} - ${slot.end}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Status indicator - always green/blue since always available
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) Color.Green else Color.Blue
                    )
            )
        }
    }
}

@Composable
private fun BookingSheetHeader(doctor: Doctor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image using ProfileImage composable
            ProfileImage(
                base64Image = doctor.profileImageBase64,
                defaultImageRes = R.drawable.doc_prof_unloaded,
                modifier = Modifier.size(56.dp),
                onImageClick = null
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Dr. ${doctor.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(doctor.specialty, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${doctor.rating} • ${doctor.reviewsCount} reviews",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}