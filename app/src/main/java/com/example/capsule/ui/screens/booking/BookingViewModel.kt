package com.example.capsule.ui.screens.booking

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.data.repository.ProfileRepository
import com.example.capsule.util.convertDateTimeToMillis
import kotlinx.coroutines.launch
import java.time.LocalDate

class BookingViewModel : ViewModel() {

    private val repository = ProfileRepository.getInstance()

    // UI State
    val selectedDate = mutableStateOf(LocalDate.now())
    val selectedSlot = mutableStateOf<TimeSlot?>(null)
    val appointmentType = mutableStateOf("In-Person")

    val allSlots = mutableStateOf<List<TimeSlot>>(emptyList())
    val availableSlots = mutableStateOf<List<TimeSlot>>(emptyList())

    val isLoading = mutableStateOf(false)
    val isBooking = mutableStateOf(false)

    val showConfirmation = mutableStateOf(false)
    val showDatePicker = mutableStateOf(false)

    // --------------------------------------------------------------------
    //                  LOAD DOCTOR SLOTS + APPLY FIREBASE FILTER
    // --------------------------------------------------------------------

    // --------------------------------------------------------------------
//                  LOAD DOCTOR SLOTS (NO FIREBASE FILTER)
// --------------------------------------------------------------------

    fun loadSlots(doctor: Doctor) {
        isLoading.value = true

        viewModelScope.launch {
            val dayOfWeek = selectedDate.value.dayOfWeek.name.lowercase()
                .replaceFirstChar { it.uppercase() }

            // Get the single slot for this day (if exists)
            val singleSlot = doctor.availability[dayOfWeek]?.firstOrNull()

            // either show it or empty list
            allSlots.value = if (singleSlot != null) listOf(singleSlot) else emptyList()

            // All slots are considered available (no Firebase check)
            availableSlots.value = allSlots.value

            // Auto-select the slot if there's only one
            if (allSlots.value.size == 1 && selectedSlot.value == null) {
                selectedSlot.value = allSlots.value.first()
            }

            isLoading.value = false
        }
    }

    // --------------------------------------------------------------------
    //                              BOOK APPOINTMENT
    // --------------------------------------------------------------------

    fun bookAppointment(
        doctor: Doctor,
        patient: Patient?,
        onSuccess: (Long, TimeSlot, String) -> Unit,
        onError: (String) -> Unit
    ) {

        // Validate patient
        if (patient == null) {
            onError("Please complete your profile first.")
            return
        }

        // Validate slot
        val slot = selectedSlot.value
        if (slot == null) {
            onError("Please select a time slot.")
            return
        }

        // Validate date
        if (selectedDate.value.isBefore(LocalDate.now())) {
            onError("You cannot book a past date.")
            return
        }

        isBooking.value = true

        val timestamp = convertDateTimeToMillis(selectedDate.value, slot.start)

        val appointment = Appointment(
            doctorId = doctor.id,
            patientId = patient.id,
            patientName = patient.name,
            doctorName = doctor.name,
            dateTime = timestamp,
            timeSlot = slot,
            type = appointmentType.value,
            status = "Upcoming"
        )

        // Make booking request
        viewModelScope.launch {
            repository.bookAppointment(appointment) { success ->
                isBooking.value = false
                showConfirmation.value = false

                if (success) {
                    onSuccess(timestamp, slot, appointmentType.value)
                } else {
                    onError("This time slot was just booked. Please pick another date.")
                }
            }
        }
    }

}