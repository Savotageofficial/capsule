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

    fun loadSlots(doctor: Doctor) {
        isLoading.value = true

        viewModelScope.launch {

            val dayOfWeek = selectedDate.value.dayOfWeek.name.lowercase()
                .replaceFirstChar { it.uppercase() }

            // Get the single slot for this day (if exists)
            val singleSlot = doctor.availability[dayOfWeek]?.firstOrNull()

            // Since there's only one slot per day, either show it or empty list
            allSlots.value = if (singleSlot != null) listOf(singleSlot) else emptyList()

            val dateMillis = convertDateTimeToMillis(selectedDate.value, "00:00")

            // Get available slots filtered by firebase bookings
            repository.getAvailableTimeSlots(
                doctorId = doctor.id,
                selectedDate = dateMillis,
                doctorAvailability = doctor.availability
            ) { freeSlots ->
                // Since there's only one slot, check if it's available
                availableSlots.value = freeSlots

                // Auto-select the slot if it's available and there's only one
                if (freeSlots.size == 1 && selectedSlot.value == null) {
                    selectedSlot.value = freeSlots.first()
                }

                isLoading.value = false
            }
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

    // --------------------------------------------------------------------
    //                      SIMPLIFIED SLOT SELECTION
    // --------------------------------------------------------------------

    /**
     * Since there's only one slot per day, we can simplify slot selection
     */
    fun selectSlot(slot: TimeSlot) {
        selectedSlot.value = slot
    }

    /**
     * Check if a date has availability
     */
    fun hasAvailability(doctor: Doctor, date: LocalDate): Boolean {
        val dayOfWeek = date.dayOfWeek.name.lowercase()
            .replaceFirstChar { it.uppercase() }
        return doctor.availability[dayOfWeek]?.isNotEmpty() == true
    }

    // --------------------------------------------------------------------
    //                               RESET UI
    // --------------------------------------------------------------------

    fun reset() {
        selectedDate.value = LocalDate.now()
        selectedSlot.value = null
        appointmentType.value = "In-Person"

        allSlots.value = emptyList()
        availableSlots.value = emptyList()

        showConfirmation.value = false
        showDatePicker.value = false
    }

    /**
     * Reset only the slot selection (useful when changing dates)
     */
    fun resetSlotSelection() {
        selectedSlot.value = null
    }
}