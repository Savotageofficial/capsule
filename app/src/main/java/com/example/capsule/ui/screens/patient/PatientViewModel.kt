package com.example.capsule.ui.screens.patient

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Patient
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class PatientViewModel : ViewModel() {
    private val repo = ProfileRepository.getInstance()

    private val _patient = mutableStateOf<Patient?>(null)
    val patient = _patient

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    private val _appointments = mutableStateOf(emptyList<Appointment>())
    val appointments = _appointments

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage = _errorMessage

    fun loadCurrentPatientProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getCurrentPatient { patient ->
                _patient.value = patient
                if (patient != null) {
                    loadPatientAppointments()
                } else {
                    _isLoading.value = false
                    _errorMessage.value = "Failed to load patient profile"
                }
            }
        }
    }

    fun loadPatientProfileById(patientId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getPatientById(patientId) { patient ->
                _patient.value = patient
                _isLoading.value = false
                if (patient == null) {
                    _errorMessage.value = "Patient not found"
                }
            }
        }
    }

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

    fun loadPatientAppointments() {
        _patient.value?.id?.let { patientId ->
            _isLoading.value = true
            viewModelScope.launch {
                repo.getPatientAppointments(patientId) { appointments ->
                    _appointments.value = appointments
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

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            repo.updateAppointmentStatus(appointmentId, "Cancelled") { success ->
                if (success) {
                    // Update local state by filtering out the cancelled appointment
                    _appointments.value = _appointments.value.filter { it.id != appointmentId }
                } else {
                    _errorMessage.value = "Failed to cancel appointment"
                }
            }
        }
    }

    // when add refresh
    fun refreshAppointments() {
        loadPatientAppointments()
    }
}