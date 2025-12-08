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
    // Appointments Logic
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
                    // Update all appointments
                    _allAppointments.value = _allAppointments.value.map { a ->
                        if (a.id == appointmentId) a.copy(status = "Cancelled") else a
                    }
                    // Reapply current filter
                    applyFilter(_filterState.value)
                } else {
                    errorMessage.value = "Failed to cancel appointment"
                }
            }
        }
    }

    // ------------------------------
    // Enrich appointments with profile images (SIMPLIFIED)
    // ------------------------------
    protected fun loadAndEnrichAppointments(
        fetchAppointments: (callback: (List<Appointment>) -> Unit) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            fetchAppointments { appointments ->
                if (appointments.isEmpty()) {
                    _allAppointments.value = emptyList()
                    _appointments.value = emptyList()
                    _isLoading.value = false
                    return@fetchAppointments
                }

                // Start enriching appointments with profile images
                val enrichedAppointments = mutableListOf<Appointment>()
                val total = appointments.size
                var completed = 0

                appointments.forEach { appointment ->
                    // Fetch doctor profile image
                    repo.getDoctorProfileImage(appointment.doctorId) { doctorImage ->
                        // Fetch patient profile image
                        repo.getPatientProfileImage(appointment.patientId) { patientImage ->
                            synchronized(enrichedAppointments) {
                                enrichedAppointments.add(
                                    appointment.copy(
                                        doctorProfileImage = doctorImage,
                                        patientProfileImage = patientImage
                                    )
                                )
                                completed++

                                // When all appointments are enriched
                                if (completed == total) {
                                    // Sort to maintain original order
                                    val sorted = enrichedAppointments.sortedBy { app ->
                                        appointments.indexOfFirst { it.id == app.id }
                                    }

                                    // Update state
                                    _allAppointments.value = sorted
                                    applyFilter(_filterState.value)
                                    _isLoading.value = false
                                }
                            }
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