package com.example.capsule.ui.screens.doctor

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Prescription
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {

    private val profileRepository = ProfileRepository.getInstance()

    // Doctor Profile
    private val _doctor = mutableStateOf<Doctor?>(null)
    val doctor = _doctor

    // Appointments
    private val _allAppointments = mutableStateOf(emptyList<Appointment>())

    private val _appointments = mutableStateOf(emptyList<Appointment>())
    val appointments = _appointments

    // Filter state
    private val _filterState = mutableStateOf("Upcoming") // Default filter
    val filterState = _filterState

    // Prescriptions
    private val _prescriptions = mutableStateOf(emptyList<Prescription>())
    val prescriptions = _prescriptions

    // Selected prescription for viewing
    private val _selectedPrescription = mutableStateOf<Prescription?>(null)
    val selectedPrescription = _selectedPrescription

    // Loading state
    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    // Error message
    private val _errorMessage = mutableStateOf<String?>(null)

    // Availability (State kept separately for the UI)
    val availability = mutableStateMapOf<String, MutableList<TimeSlot>>()

    // Rating state
    private val _hasRated = mutableStateOf(false)
    val hasRated = _hasRated

    private val _currentUserRating = mutableStateOf(0)
    val currentUserRating = _currentUserRating

    private val _isRatingLoading = mutableStateOf(false)
    val isRatingLoading = _isRatingLoading

    // -------------------------------------------------------------
    // Load Doctor Profile
    // -------------------------------------------------------------
    fun loadCurrentDoctorProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getCurrentDoctor { doctor ->
                _doctor.value = doctor
                if (doctor != null) {
                    loadAvailability()
                    loadDoctorAppointments()
                    loadDoctorPrescriptions()
                }
                _isLoading.value = false
            }
        }
    }

    fun loadDoctorProfileById(doctorId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getDoctorById(doctorId) { doctor ->
                _doctor.value = doctor
                if (doctor != null) {
                    loadAvailability()
                    loadDoctorAppointments()
                    loadDoctorPrescriptions()
                }
                _isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------------
    // Filter Functions
    // -------------------------------------------------------------
    fun setFilter(filter: String) {
        _filterState.value = filter
        applyFilter(filter)
    }

    private fun applyFilter(filter: String) {
        _appointments.value = when (filter) {
            "Upcoming" -> _allAppointments.value.filter { it.status == "Upcoming" }
            "Completed" -> _allAppointments.value.filter { it.status == "Completed" }
            "Cancelled" -> _allAppointments.value.filter { it.status == "Cancelled" }
            else -> _allAppointments.value
        }.sortedBy { it.dateTime }
    }

    // -------------------------------------------------------------
    // Load Appointments
    // -------------------------------------------------------------
    private fun loadDoctorAppointments() {
        _doctor.value?.id?.let { doctorId ->
            viewModelScope.launch {
                profileRepository.getDoctorAppointments(doctorId) { appointments ->
                    _allAppointments.value = appointments
                    applyFilter(_filterState.value) // Apply the current filter
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Mark Appointment as Completed
    // -------------------------------------------------------------
    fun markAsCompleted(appointmentId: String) {
        viewModelScope.launch {
            profileRepository.updateAppointmentStatus(appointmentId, "Completed") { success ->
                if (success) {
                    // Update local state
                    _allAppointments.value = _allAppointments.value.map { appointment ->
                        if (appointment.id == appointmentId) {
                            appointment.copy(status = "Completed")
                        } else {
                            appointment
                        }
                    }
                    applyFilter(_filterState.value) // Reapply filter
                } else {
                    _errorMessage.value = "Failed to mark appointment as completed"
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Prescription Management
    // -------------------------------------------------------------
    fun loadDoctorPrescriptions() {
        _doctor.value?.id?.let { doctorId ->
            _isLoading.value = true
            viewModelScope.launch {
                profileRepository.getPrescriptionsByDoctor(doctorId) { prescriptions ->
                    _prescriptions.value = prescriptions
                    _isLoading.value = false
                    if (prescriptions.isEmpty()) {
                        _errorMessage.value = "No prescriptions found"
                    }
                }
            }
        } ?: run {
            _errorMessage.value = "Doctor ID not available"
            _isLoading.value = false
        }
    }

    fun loadPrescriptionById(prescriptionId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getPrescriptionById(prescriptionId) { prescription ->
                _selectedPrescription.value = prescription
                _isLoading.value = false
                if (prescription == null) {
                    _errorMessage.value = "Prescription not found"
                }
            }
        }
    }


    fun deletePrescription(prescriptionId: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.deletePrescription(prescriptionId) { success ->
                if (success) {
                    // Remove from local state
                    _prescriptions.value = _prescriptions.value.filter { it.id != prescriptionId }
                    // Clear selected if it's the one being deleted
                    if (_selectedPrescription.value?.id == prescriptionId) {
                        _selectedPrescription.value = null
                    }
                }
                onDone(success)
            }
        }
    }

    // -------------------------------------------------------------
    // Load Availability into local state
    // -------------------------------------------------------------
    private fun loadAvailability() {
        availability.clear()

        _doctor.value?.availability?.forEach { (day, slots) ->
            availability[day] = slots.toMutableList()
        }
    }

    // -------------------------------------------------------------
    // Availability Editing Functions
    // -------------------------------------------------------------

    fun addSlot(day: String) {
        // Only add if no slot exists for this day
        if (availability[day].isNullOrEmpty()) {
            availability.putIfAbsent(day, mutableListOf())
            availability[day]?.add(TimeSlot("09:00", "17:00"))
        }
    }

    fun updateSlot(day: String, index: Int, newSlot: TimeSlot) {
        availability[day]?.let { slots ->
            if (index in slots.indices) {
                slots[index] = newSlot
            }
        }
    }

    fun deleteSlot(day: String, index: Int) {
        availability[day]?.let { slots ->
            if (index in slots.indices) {
                slots.removeAt(index)

                // Delete day if empty
                if (slots.isEmpty()) {
                    availability.remove(day)
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Save Availability to Firestore
    // -------------------------------------------------------------
    fun saveAvailability(onDone: (Boolean) -> Unit) {
        val cleanMap = availability.mapValues { it.value.toList() }

        val data = mapOf("availability" to cleanMap)

        viewModelScope.launch {
            profileRepository.updateCurrentDoctor(data) { success ->
                if (success) {
                    _doctor.value = _doctor.value?.copy(availability = cleanMap)
                }
                onDone(success)
            }
        }
    }

    // -------------------------------------------------------------
    // Update Other Doctor Profile Fields
    // -------------------------------------------------------------
    fun updateDoctorProfile(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.updateCurrentDoctor(data) { success ->
                if (success) {
                    _doctor.value = _doctor.value?.copy(
                        name = data["name"] as? String ?: _doctor.value!!.name,
                        specialty = data["specialty"] as? String ?: _doctor.value!!.specialty,
                        bio = data["bio"] as? String ?: _doctor.value!!.bio,
                        experience = data["experience"] as? String ?: _doctor.value!!.experience,
                        clinicName = data["clinicName"] as? String ?: _doctor.value!!.clinicName,
                        clinicAddress = data["clinicAddress"] as? String
                            ?: _doctor.value!!.clinicAddress,
                        locationUrl = data["locationUrl"] as? String ?: _doctor.value!!.locationUrl,
                        sessionPrice = data["sessionPrice"] as? Double
                            ?: _doctor.value!!.sessionPrice,
                        availability = (data["availability"] as? Map<String, List<TimeSlot>>)
                            ?: _doctor.value!!.availability
                    )
                }
                onDone(success)
            }
        }
    }

    // -------------------------------------------------------------
    // cancel Appointment
    // -------------------------------------------------------------
    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            profileRepository.updateAppointmentStatus(appointmentId, "Cancelled") { success ->
                if (success) {
                    // Update local state
                    _allAppointments.value = _allAppointments.value.map { appointment ->
                        if (appointment.id == appointmentId) {
                            appointment.copy(status = "Cancelled")
                        } else {
                            appointment
                        }
                    }
                    applyFilter(_filterState.value) // Reapply filter
                } else {
                    _errorMessage.value = "Failed to cancel appointment"
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Rating Functions
    // -------------------------------------------------------------

    // Function to check if current user has rated
    fun checkIfUserHasRated(doctorId: String, userId: String) {
        _isRatingLoading.value = true
        viewModelScope.launch {
            profileRepository.hasUserRatedDoctor(doctorId, userId) { hasRated ->
                _hasRated.value = hasRated
                _isRatingLoading.value = false
            }
        }
    }

    // Function to submit rating
    fun submitRating(doctorId: String, patientId: String, rating: Int, onResult: (Boolean) -> Unit) {
        _isRatingLoading.value = true
        viewModelScope.launch {
            profileRepository.rateDoctor(doctorId, patientId, rating) { success ->
                if (success) {
                    // Update local doctor state
                    _doctor.value?.let { currentDoctor ->
                        val newTotalRating = currentDoctor.totalRating + rating
                        val newReviewsCount = currentDoctor.reviewsCount + 1
                        val newRating = newTotalRating / newReviewsCount.toDouble()

                        _doctor.value = currentDoctor.copy(
                            totalRating = newTotalRating,
                            reviewsCount = newReviewsCount,
                            rating = newRating,
                            ratedByUsers = currentDoctor.ratedByUsers + patientId
                        )
                        _hasRated.value = true
                        _currentUserRating.value = rating
                    }
                }
                _isRatingLoading.value = false
                onResult(success)
            }
        }
    }
}