package com.example.capsule.ui.screens.patient

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Patient
import com.example.capsule.data.model.Prescription
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class PatientViewModel : ViewModel() {
    private val repo = ProfileRepository.getInstance()

    private val _patient = mutableStateOf<Patient?>(null)
    val patient = _patient

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    private val _allAppointments = mutableStateOf(emptyList<Appointment>())

    private val _appointments = mutableStateOf(emptyList<Appointment>())
    val appointments = _appointments

    private val _filterState = mutableStateOf("Upcoming") // Default filter
    val filterState = _filterState

    private val _prescriptions = mutableStateOf(emptyList<Prescription>())
    val prescriptions = _prescriptions


    private val _selectedPrescription = mutableStateOf<Prescription?>(null)
    val selectedPrescription = _selectedPrescription

    private val _errorMessage = mutableStateOf<String?>(null)


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
                    _errorMessage.value = "Failed to load patient profile"
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
                    _errorMessage.value = "Patient not found"
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
                        name = data["name"] as? String ?: _patient.value!!.name,
                        dob = data["dob"] as? Long ?: _patient.value!!.dob,
                        gender = data["gender"] as? String ?: _patient.value!!.gender,
                        contact = data["contact"] as? String ?: _patient.value!!.contact,
                        email = data["email"] as? String ?: _patient.value!!.email
                    )
                }
                onDone(success)
            }
        }
    }

    // -------------------------------------------------------------
    // Load Patient Appointments
    // -------------------------------------------------------------
    fun loadPatientAppointments() {
        _patient.value?.id?.let { patientId ->
            _isLoading.value = true
            viewModelScope.launch {
                repo.getPatientAppointments(patientId) { appointments ->
                    _allAppointments.value = appointments
                    applyFilter(_filterState.value)
                    _isLoading.value = false
                    if (appointments.isEmpty()) {
                        _errorMessage.value = "No appointments found"
                    }
                }
            }
        } ?: run {
            _errorMessage.value = "Patient ID not available"
            _isLoading.value = false
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
    // Cancel Appointment
    // -------------------------------------------------------------
    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            repo.updateAppointmentStatus(appointmentId, "Cancelled") { success ->
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
                        _errorMessage.value = "No prescriptions found"
                    }
                }
            }
        } ?: run {
            _errorMessage.value = "Patient ID not available"
            _isLoading.value = false
        }
    }

    fun loadPrescriptionById(prescriptionId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getPrescriptionById(prescriptionId) { prescription ->
                _selectedPrescription.value = prescription
                _isLoading.value = false
                if (prescription == null) {
                    _errorMessage.value = "Prescription not found"
                }
            }
        }
    }
}