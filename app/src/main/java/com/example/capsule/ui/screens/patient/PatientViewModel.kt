package com.example.capsule.ui.screens.patient

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Patient
import com.example.capsule.ui.screens.common.UserViewModel
import kotlinx.coroutines.launch

class PatientViewModel : UserViewModel("Patient") {

    private val _patient = mutableStateOf<Patient?>(null)
    val patient = _patient

    // patientId property
    private val patientId: String?
        get() = _patient.value?.id

    override fun getCurrentUserId(): String? = _patient.value?.id
    // -------------------------------------------------------------
    // Load Patient Profile
    // -------------------------------------------------------------
    fun loadCurrentPatientProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getCurrentPatient { patient ->
                _patient.value = patient
                if (patient != null) {
                    loadPatientAppointments()
                    loadPatientPrescriptions()
                } else {
                    _isLoading.value = false
                    errorMessage.value = "Failed to load patient profile"
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Load Patient Profile by ID
    // -------------------------------------------------------------
    fun loadPatientProfileById(patientId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getPatientById(patientId) { patient ->
                _patient.value = patient
                if (patient != null) {
                    loadPatientAppointments()
                    loadPatientPrescriptions()
                }
                _isLoading.value = false
                if (patient == null) {
                    errorMessage.value = "Patient not found"
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Update Patient Profile
    // -------------------------------------------------------------
    fun updatePatientProfile(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            repo.updateCurrentPatient(data) { success ->
                if (success) {
                    _patient.value = _patient.value?.copy(
                        name = data["name"] as? String ?: _patient.value?.name.orEmpty(),
                        dob = data["dob"] as? Long ?: _patient.value?.dob ?: 0L,
                        gender = data["gender"] as? String ?: _patient.value?.gender.orEmpty(),
                        contact = data["contact"] as? String ?: _patient.value?.contact.orEmpty(),
                        email = data["email"] as? String ?: _patient.value?.email.orEmpty(),
                        profileImageBase64 = data["profileImageBase64"] as? String
                            ?: _patient.value?.profileImageBase64
                    )

                }
                onDone(success)
            }
        }
    }

    // -------------------------------------------------------------
    // Load Patient Appointments WITH PROFILE IMAGES
    // -------------------------------------------------------------
    fun loadPatientAppointments() {
        _patient.value?.id?.let { patientId ->
            _isLoading.value = true
            viewModelScope.launch {
                repo.getPatientAppointments(patientId) { appointments ->
                    // Enrich appointments with profile images
                    enrichAppointmentsWithProfileImages(appointments) { enrichedAppointments ->
                        _allAppointments.value = enrichedAppointments
                        applyFilter(_filterState.value)
                        _isLoading.value = false

                        if (enrichedAppointments.isEmpty()) {
                            errorMessage.value = "No appointments found"
                        }
                    }
                }
            }
        } ?: run {
            errorMessage.value = "Patient ID not available"
            _isLoading.value = false
        }
    }

    // -------------------------------------------------------------
    // Prescription Management
    // -------------------------------------------------------------
    fun loadPatientPrescriptions() {
        _patient.value?.id?.let { patientId ->
            _isLoading.value = true
            viewModelScope.launch {
                repo.getPrescriptionsByPatient(patientId) { prescriptions ->
                    _prescriptions.value = prescriptions
                    _isLoading.value = false
                    if (prescriptions.isEmpty()) {
                        errorMessage.value = "No prescriptions found"
                    }
                }
            }
        } ?: run {
            errorMessage.value = "Patient ID not available"
            _isLoading.value = false
        }
    }

    // -------------------------------------------------------------
    // Profile Image Wrapper Methods
    // -------------------------------------------------------------
    fun uploadProfileImage(base64Image: String, callback: (Boolean, String?) -> Unit) {
        patientId?.let { id ->
            super.uploadProfileImage(id, base64Image, callback)
        } ?: run {
            callback(false, "Patient ID not available")
        }
    }

    fun deleteProfileImage(callback: (Boolean, String?) -> Unit) {
        patientId?.let { id ->
            super.deleteProfileImage(id, callback)
        } ?: run {
            callback(false, "Patient ID not available")
        }
    }


    // -------------------------------------------------------------
    // Refresh Appointments (for when profile images might have changed)
    // -------------------------------------------------------------
    fun refreshAppointments() {
        loadPatientAppointments()
    }
}