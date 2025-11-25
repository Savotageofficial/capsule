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

class PatientProfileViewModel : ViewModel() {
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

    fun bookAppointment(
        doctor: Doctor,
        dateTime: Long,
        slot: TimeSlot,
        type: String,
        onDone: (Boolean) -> Unit
    ) {
        val appointment = Appointment(
            doctorId = doctor.id,
            patientId = _patient.value?.id ?: "",
            doctorName = doctor.name,
            patientName = _patient.value?.name ?: "",
            dateTime = dateTime,
            timeSlot = slot,
            type = type,
            status = "Upcoming"
        )

        viewModelScope.launch {
            repo.bookAppointment(appointment) { success ->
                if (success) {
                    loadPatientAppointments() // Refresh appointments
                }
                onDone(success)
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

/*package com.example.capsule.ui.screens.patient

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class PatientProfileViewModel : ViewModel() {
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
                // Auto-load appointments when patient loads
                it?.id?.let { patientId ->
                    loadPatientAppointments()
                }
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

    // ENHANCED: Book appointment with better error handling
    fun bookAppointment(
        doctor: Doctor,
        dateTime: Long,
        slot: TimeSlot,
        type: String,
        onDone: (Boolean) -> Unit
    ) {
        val currentPatient = _patient.value
        if (currentPatient == null) {
            onDone(false)
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
            status = "Upcoming" // FIXED: Consistent status
        )

        viewModelScope.launch {
            // Use enhanced booking with conflict checking
            repo.bookAppointment(appointment) { success, errorMessage ->
                if (success) {
                    // Add to local list immediately
                    _appointments.value = (_appointments.value + appointment).sortedBy { it.dateTime }
                    // Refresh from server to get the actual ID
                    loadPatientAppointments()
                }
                onDone(success)

                // You could expose errorMessage to UI if needed
                if (errorMessage != null) {
                    // Handle error message (could add a state for this)
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
}*/