package com.example.capsule.ui.screens.doctor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class DoctorProfileViewModel : ViewModel() {

    private val profileRepository = ProfileRepository.getInstance()

    private val _doctor = mutableStateOf<Doctor?>(null)
    val doctor = _doctor

    private val _appointments = mutableStateOf(emptyList<Appointment>())
    val appointments = _appointments

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading
    // Simple dummy data
    private val dummyAppointments = listOf(
        Appointment(
            id = "1", patientName = "Sarah Johnson", patientId = "patient123",
            time = "10:00 AM", type = "In-Person", status = "Upcoming"
        ),
        Appointment(
            id = "2", patientName = "Mike Wilson", patientId = "patient456",
            time = "2:30 PM", type = "Video Call", status = "Upcoming"
        )
    )

    // Load current doctor
    fun loadCurrentDoctorProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getCurrentDoctor { doctor ->
                _doctor.value = doctor
                _appointments.value = dummyAppointments // Load appointments immediately
                _isLoading.value = false
            }
        }
    }

    // Load doctor by ID
    fun loadDoctorProfileById(doctorId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getDoctorById(doctorId) { doctor ->
                _doctor.value = doctor
                _appointments.value = dummyAppointments
                _isLoading.value = false
            }
        }
    }

    // Update doctor profile
    fun updateDoctorProfile(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.updateCurrentDoctor(data) { success ->
                if (success) {
                    // Update local state
                    _doctor.value = _doctor.value?.copy(
                        name = data["name"] as? String ?: _doctor.value!!.name,
                        specialty = data["specialty"] as? String ?: _doctor.value!!.specialty,
                        bio = data["bio"] as? String ?: _doctor.value!!.bio,
                        experience = data["experience"] as? String ?: _doctor.value!!.experience,
                        clinicName = data["clinicName"] as? String ?: _doctor.value!!.clinicName,
                        clinicAddress = data["clinicAddress"] as? String ?: _doctor.value!!.clinicAddress,
                        locationUrl = data["locationUrl"] as? String ?: _doctor.value!!.locationUrl,
                        availability = data["availability"] as? String ?: _doctor.value!!.availability
                    )
                }
                onDone(success)
            }
        }
    }

    // Delete appointment
    fun deleteAppointment(appointmentId: String) {
        _appointments.value = _appointments.value.filter { it.id != appointmentId }

        // TODO: Later add Firestore deletion
        // profileRepository.deleteAppointment(appointmentId) { success ->
        //     if (success) {
        //         // Update local state
        //         val updated = _appointments.value.toMutableList()
        //         updated.removeAll { it.id == appointmentId }
        //         _appointments.value = updated
        //     }
        // }
    }

    //   GET TODAY'S APPOINTMENTS for later
    fun getTodaysAppointments(): List<Appointment> {
        return _appointments.value.take(3) // Use state appointments, not upcomingAppointments
    }
}