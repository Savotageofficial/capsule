package com.example.capsule.ui.screens.patient

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.example.capsule.data.model.TimeSlot
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

    fun loadCurrentPatientProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getCurrentPatient {
                _patient.value = it
                _isLoading.value = false
            }
        }
    }

    fun loadPatientProfileById(patientId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getPatientById(patientId) {
                _patient.value = it
                _isLoading.value = false
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
            viewModelScope.launch {
                repo.getPatientAppointments(patientId) { appointments ->
                    _appointments.value = appointments
                }
            }
        }
    }

    // Enhanced bookAppointment method
    fun bookAppointment(
        doctor: Doctor,
        dateTime: Long,
        slot: TimeSlot,
        type: String,
        onDone: (Boolean, String?) -> Unit // Returns success status and optional appointment ID
    ) {
        val currentPatient = _patient.value
        if (currentPatient == null) {
            onDone(false, "Patient not loaded")
            return
        }

        val appointment = Appointment(
            doctorId = doctor.id,
            patientId = currentPatient.id,
            doctorName = doctor.name,
            patientName = currentPatient.name,
            dateTime = dateTime,
            timeSlot = slot,
            type = type,
            status = "Upcoming"
        )

        viewModelScope.launch {
            repo.bookAppointment(appointment) { success ->
                if (success) {
                    // Refresh appointments list
                    loadPatientAppointments()
                    onDone(true, appointment.id)
                } else {
                    onDone(false, "Failed to book appointment")
                }
            }
        }
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            repo.updateAppointmentStatus(appointmentId, "Cancelled") { success ->
                if (success) {
                    // Remove from local list
                    _appointments.value = _appointments.value.filter { it.id != appointmentId }
                }
            }
        }
    }
}
