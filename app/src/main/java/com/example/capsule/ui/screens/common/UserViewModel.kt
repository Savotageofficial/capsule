package com.example.capsule.ui.screens.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Prescription
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

open class UserViewModel(
    private val userType: String // "Doctor" or "Patient"
) : ViewModel() {

    // ------------------------------
    // Repository
    // ------------------------------
    protected val repo = ProfileRepository.getInstance()

    // ------------------------------
    // Loading & Error States
    // ------------------------------
    protected val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    protected val errorMessage = mutableStateOf<String?>(null)


    // Add this helper method
    protected open fun getCurrentUserId(): String? = null

    // ------------------------------
    // Profile Image Logic
    // ------------------------------
    open fun uploadProfileImage(
        userId: String? = null,
        base64Image: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val finalUserId = userId ?: getCurrentUserId()
        if (finalUserId == null) {
            callback(false, "User ID not available")
            return
        }

        viewModelScope.launch {
            try {
                repo.uploadProfileImage(
                    userId = finalUserId,
                    userType = userType,
                    base64Image = base64Image,
                    onSuccess = { callback(true, null) },
                    onFailure = { error -> callback(false, error) }
                )
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }

    open fun deleteProfileImage(
        userId: String? = null,
        callback: (Boolean, String?) -> Unit
    ) {
        val finalUserId = userId ?: getCurrentUserId()
        if (finalUserId == null) {
            callback(false, "User ID not available")
            return
        }

        viewModelScope.launch {
            try {
                repo.deleteProfileImage(
                    userId = finalUserId,
                    userType = userType,
                    onSuccess = { callback(true, null) },
                    onFailure = { error -> callback(false, error) }
                )
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }

    // ------------------------------
    // Helper methods for fetching profile images
    // ------------------------------
    protected fun getDoctorProfileImage(doctorId: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            repo.getDoctorProfileImage(doctorId) { image ->
                onResult(image)
            }
        }
    }

    protected fun getPatientProfileImage(patientId: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            repo.getPatientProfileImage(patientId) { image ->
                onResult(image)
            }
        }
    }

    // ------------------------------
    // Appointments Logic with Profile Images
    // ------------------------------
    protected val _allAppointments = mutableStateOf(emptyList<Appointment>())
    protected val _appointments = mutableStateOf(emptyList<Appointment>())
    val appointments = _appointments

    protected val _filterState = mutableStateOf("Upcoming")
    val filterState = _filterState

    fun setFilter(filter: String) {
        _filterState.value = filter
        applyFilter(filter)
    }

    protected fun applyFilter(filter: String) {
        _appointments.value = when (filter) {
            "Upcoming" -> _allAppointments.value.filter { it.status == "Upcoming" }
            "Completed" -> _allAppointments.value.filter { it.status == "Completed" }
            "Cancelled" -> _allAppointments.value.filter { it.status == "Cancelled" }
            else -> _allAppointments.value
        }.sortedBy { it.dateTime }
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            repo.updateAppointmentStatus(appointmentId, "Cancelled") { success ->
                if (success) {
                    _allAppointments.value = _allAppointments.value.map { a ->
                        if (a.id == appointmentId) a.copy(status = "Cancelled") else a
                    }
                    applyFilter(_filterState.value)
                } else {
                    errorMessage.value = "Failed to cancel appointment"
                }
            }
        }
    }

    // ------------------------------
    // Method to enrich appointments with profile images
    // ------------------------------
    protected fun enrichAppointmentsWithProfileImages(
        appointments: List<Appointment>,
        onComplete: (List<Appointment>) -> Unit
    ) {
        val enrichedAppointments = mutableListOf<Appointment>()

        if (appointments.isEmpty()) {
            onComplete(emptyList())
            return
        }

        // Create a copy to avoid concurrent modification
        val appointmentsCopy = appointments.toList()

        appointmentsCopy.forEachIndexed { index, appointment ->
            // Fetch both images in parallel using coroutines
            viewModelScope.launch {
                // Use suspend functions if available, or use callbacks
                var doctorImage: String? = null
                var patientImage: String? = null

                // Fetch doctor image
                repo.getDoctorProfileImage(appointment.doctorId) { image ->
                    doctorImage = image

                    // Check if both images are fetched
                    if (patientImage != null) {
                        val enriched = appointment.copy(
                            doctorProfileImage = doctorImage,
                            patientProfileImage = patientImage
                        )
                        enrichedAppointments.add(enriched)

                        // Check if all appointments are processed
                        if (enrichedAppointments.size == appointmentsCopy.size) {
                            onComplete(enrichedAppointments)
                        }
                    }
                }

                // Fetch patient image
                repo.getPatientProfileImage(appointment.patientId) { image ->
                    patientImage = image

                    // Check if both images are fetched
                    if (doctorImage != null) {
                        val enriched = appointment.copy(
                            doctorProfileImage = doctorImage,
                            patientProfileImage = patientImage
                        )
                        enrichedAppointments.add(enriched)

                        // Check if all appointments are processed
                        if (enrichedAppointments.size == appointmentsCopy.size) {
                            onComplete(enrichedAppointments)
                        }
                    }
                }
            }
        }
    }

    // ------------------------------
    // Prescriptions Logic
    // ------------------------------
    protected val _prescriptions = mutableStateOf(emptyList<Prescription>())
    val prescriptions = _prescriptions

    protected val _selectedPrescription = mutableStateOf<Prescription?>(null)
    val selectedPrescription = _selectedPrescription

    fun loadPrescriptionById(prescriptionId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getPrescriptionById(prescriptionId) { prescription ->
                _selectedPrescription.value = prescription
                _isLoading.value = false
                if (prescription == null) {
                    errorMessage.value = "Prescription not found"
                }
            }
        }
    }

    fun clearSelectedPrescription() {
        _selectedPrescription.value = null
    }
}