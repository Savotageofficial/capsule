package com.example.capsule.ui.screens.doctor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.TimeSlot
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


    // Load current doctor profile
    fun loadCurrentDoctorProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getCurrentDoctor { doctor ->
                _doctor.value = doctor
                loadDoctorAppointments() // Load real appointments
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
                loadDoctorAppointments() // Load real appointments
                _isLoading.value = false
            }
        }
    }

    // Load appointments from Firestore
    private fun loadDoctorAppointments() {
        _doctor.value?.id?.let { doctorId ->
            viewModelScope.launch {
                profileRepository.getDoctorAppointments(doctorId) { appointments ->
                    _appointments.value = appointments
                }
            }
        }
    }

    // Update doctor profile
    fun updateDoctorProfile(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.updateCurrentDoctor(data) { success ->
                if (success) {
                    // Update local state with proper type casting
                    _doctor.value = _doctor.value?.copy(
                        name = data["name"] as? String ?: _doctor.value!!.name,
                        specialty = data["specialty"] as? String ?: _doctor.value!!.specialty,
                        bio = data["bio"] as? String ?: _doctor.value!!.bio,
                        experience = data["experience"] as? String ?: _doctor.value!!.experience,
                        clinicName = data["clinicName"] as? String ?: _doctor.value!!.clinicName,
                        clinicAddress = data["clinicAddress"] as? String
                            ?: _doctor.value!!.clinicAddress,
                        locationUrl = data["locationUrl"] as? String ?: _doctor.value!!.locationUrl,
                        availability = (data["availability"] as? Map<String, List<TimeSlot>>)
                            ?: _doctor.value!!.availability
                    )
                }
                onDone(success)
            }
        }
    }

    // Update availability specifically
    fun updateAvailability(updatedAvailability: Map<String, List<TimeSlot>>) {
        viewModelScope.launch {
            val data = mapOf("availability" to updatedAvailability)
            profileRepository.updateCurrentDoctor(data) { success ->
                if (success) {
                    _doctor.value = _doctor.value?.copy(availability = updatedAvailability)
                }
            }
        }
    }

    // Delete appointment
    fun deleteAppointment(appointmentId: String) {
        viewModelScope.launch {
            profileRepository.deleteAppointment(appointmentId) { success ->
                if (success) {
                    // Update local state
                    _appointments.value = _appointments.value.filter { it.id != appointmentId }
                }
                // TODO: Show error message if deletion fails
            }
        }
    }

}