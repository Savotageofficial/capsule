package com.example.capsule.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.ui.screens.doctor.DoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailabilityBottomSheet(
    show: Boolean,
    viewModel: DoctorViewModel,
    onDismiss: () -> Unit
) {
    if (!show) return

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                "Edit Availability",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            val days = listOf(
                "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday"
            )

            days.forEach { day ->

                val slots = viewModel.availability[day] ?: emptyList()

                DayAvailabilityCard(
                    day = day,
                    slots = slots,
                    onAddSlot = { viewModel.addSlot(day) },
                    onSlotUpdated = { index, updated ->
                        viewModel.updateSlot(day, index, updated)
                    },
                    onSlotDeleted = { index ->
                        viewModel.deleteSlot(day, index)
                    }
                )

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        viewModel.saveAvailability { success ->
                            if (success) onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}


@Composable
fun DayAvailabilityCard(
    day: String,
    slots: List<TimeSlot>,
    onAddSlot: () -> Unit,
    onSlotUpdated: (Int, TimeSlot) -> Unit,
    onSlotDeleted: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(day, fontSize = 18.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onAddSlot) {
                    Icon(Icons.Default.Add, contentDescription = "Add time slot")
                }
            }

            if (slots.isEmpty()) {
                Text(
                    "No working hours",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                slots.forEachIndexed { index, slot ->
                    TimeSlotEditor(
                        slot = slot,
                        onChange = { onSlotUpdated(index, it) },
                        onDelete = { onSlotDeleted(index) }
                    )
                    if (index < slots.size - 1) Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TimeSlotEditor(
    slot: TimeSlot,
    onChange: (TimeSlot) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePickerField(
                    label = "Start",
                    time = slot.start,
                    onTimeSelected = {
                        onChange(slot.copy(start = it))
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Text("to", color = Color.Gray)

                Spacer(Modifier.width(8.dp))

                TimePickerField(
                    label = "End",
                    time = slot.end,
                    onTimeSelected = {
                        onChange(slot.copy(end = it))
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete time slot",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    label: String,
    time: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    // Parse current time for the time picker
    val currentTime = remember(time) {
        val parts = time.split(":")
        if (parts.size == 2) {
            parts[0].toIntOrNull() to parts[1].toIntOrNull()
        } else {
            9 to 0 // Default to 9:00 AM
        }
    }

    val pickerState = rememberTimePickerState(
        initialHour = currentTime.first ?: 9,
        initialMinute = currentTime.second ?: 0
    )

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text(time)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select $label Time") },
            text = {
                TimePicker(state = pickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hour = pickerState.hour
                        val minute = pickerState.minute
                        val formattedTime = String.format("%02d:%02d", hour, minute)
                        onTimeSelected(formattedTime)
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}