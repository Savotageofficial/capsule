package com.example.capsule.ui.screens.doctor

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.ui.screens.common.UserViewModel
import kotlinx.coroutines.launch

class DoctorViewModel : UserViewModel(userType = "Doctor") {

    // Doctor Profile
    private val _doctor = mutableStateOf<Doctor?>(null)
    val doctor = _doctor

    // doctorId property
    private val doctorId: String?
        get() = _doctor.value?.id


    // Availability (State kept separately for the UI)
    val availability = mutableStateMapOf<String, MutableList<TimeSlot>>()

    // Rating state
    private val _hasRated = mutableStateOf(false)
    val hasRated = _hasRated

    private val _currentUserRating = mutableIntStateOf(0)
    val currentUserRating = _currentUserRating

    private val _isRatingLoading = mutableStateOf(false)
    val isRatingLoading = _isRatingLoading

    override fun getCurrentUserId(): String? = _doctor.value?.id

    // -------------------------------------------------------------
    // Load Doctor Profile
    // -------------------------------------------------------------
    fun loadCurrentDoctorProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getCurrentDoctor { doctor ->
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
            repo.getDoctorById(doctorId) { doctor ->
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
    // Load Appointments WITH PROFILE IMAGES
    // -------------------------------------------------------------
    private fun loadDoctorAppointments() {
        _doctor.value?.id?.let { doctorId ->
            viewModelScope.launch {
                repo.getDoctorAppointments(doctorId) { appointments ->
                    // Enrich appointments with profile images
                    enrichAppointmentsWithProfileImages(appointments) { enrichedAppointments ->
                        _allAppointments.value = enrichedAppointments
                        applyFilter(_filterState.value) // Apply the current filter
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Mark Appointment as Completed
    // -------------------------------------------------------------
    fun markAsCompleted(appointmentId: String) {
        viewModelScope.launch {
            repo.updateAppointmentStatus(appointmentId, "Completed") { success ->
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
                    errorMessage.value = "Failed to mark appointment as completed"
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
                repo.getPrescriptionsByDoctor(doctorId) { prescriptions ->
                    _prescriptions.value = prescriptions
                    _isLoading.value = false
                    if (prescriptions.isEmpty()) {
                        errorMessage.value = "No prescriptions found"
                    }
                }
            }
        } ?: run {
            errorMessage.value = "Doctor ID not available"
            _isLoading.value = false
        }
    }

    fun deletePrescription(prescriptionId: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            repo.deletePrescription(prescriptionId) { success ->
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
            repo.updateCurrentDoctor(data) { success ->
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
            repo.updateCurrentDoctor(data) { success ->
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
                            ?: _doctor.value!!.availability,
                        profileImageBase64 = data["profileImageBase64"] as? String
                            ?: _doctor.value!!.profileImageBase64
                    )
                }
                onDone(success)
            }
        }
    }


    // -------------------------------------------------------------
    // Rating Functions
    // -------------------------------------------------------------
    fun checkIfUserHasRated(doctorId: String, userId: String) {
        _isRatingLoading.value = true
        viewModelScope.launch {
            repo.hasUserRatedDoctor(doctorId, userId) { hasRated ->
                _hasRated.value = hasRated
                _isRatingLoading.value = false
            }
        }
    }

    fun submitRating(
        doctorId: String,
        patientId: String,
        rating: Int,
        onResult: (Boolean) -> Unit
    ) {
        _isRatingLoading.value = true
        viewModelScope.launch {
            repo.rateDoctor(doctorId, patientId, rating) { success ->
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
                        _currentUserRating.intValue = rating
                    }
                }
                _isRatingLoading.value = false
                onResult(success)
            }
        }
    }

    // -------------------------------------------------------------
    // Profile Image Wrapper Methods
    // -------------------------------------------------------------
    fun uploadProfileImage(base64Image: String, callback: (Boolean, String?) -> Unit) {
        doctorId?.let { id ->
            super.uploadProfileImage(id, base64Image, callback)
        } ?: run {
            callback(false, "Doctor ID not available")
        }
    }

    fun deleteProfileImage(callback: (Boolean, String?) -> Unit) {
        doctorId?.let { id ->
            super.deleteProfileImage(id, callback)
        } ?: run {
            callback(false, "Doctor ID not available")
        }
    }

}