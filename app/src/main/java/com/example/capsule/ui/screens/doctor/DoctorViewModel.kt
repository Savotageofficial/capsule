package com.example.capsule.ui.screens.doctor

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Prescription
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {

    private val profileRepository = ProfileRepository.getInstance()

    // Doctor Profile
    private val _doctor = mutableStateOf<Doctor?>(null)
    val doctor = _doctor

    // Appointments
    private val _appointments = mutableStateOf(emptyList<Appointment>())
    val appointments = _appointments

    // Prescriptions
    private val _prescriptions = mutableStateOf(emptyList<Prescription>())
    val prescriptions = _prescriptions

    // Selected prescription for viewing
    private val _selectedPrescription = mutableStateOf<Prescription?>(null)
    val selectedPrescription = _selectedPrescription

    // Loading state
    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    // Error message
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage = _errorMessage

    // Availability (State kept separately for the UI)
    val availability = mutableStateMapOf<String, MutableList<TimeSlot>>()

    // -------------------------------------------------------------
    // Load Doctor Profile
    // -------------------------------------------------------------
    fun loadCurrentDoctorProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getCurrentDoctor { doctor ->
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
            profileRepository.getDoctorById(doctorId) { doctor ->
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
    // Load Appointments
    // -------------------------------------------------------------
    private fun loadDoctorAppointments() {
        _doctor.value?.id?.let { doctorId ->
            viewModelScope.launch {
                profileRepository.getDoctorAppointments(doctorId) { appointments ->
                    _appointments.value = appointments
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
                profileRepository.getPrescriptionsByDoctor(doctorId) { prescriptions ->
                    _prescriptions.value = prescriptions
                    _isLoading.value = false
                    if (prescriptions.isEmpty()) {
                        _errorMessage.value = "No prescriptions found"
                    }
                }
            }
        } ?: run {
            _errorMessage.value = "Doctor ID not available"
            _isLoading.value = false
        }
    }

    fun loadPrescriptionById(prescriptionId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getPrescriptionById(prescriptionId) { prescription ->
                _selectedPrescription.value = prescription
                _isLoading.value = false
                if (prescription == null) {
                    _errorMessage.value = "Prescription not found"
                }
            }
        }
    }


    fun deletePrescription(prescriptionId: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.deletePrescription(prescriptionId) { success ->
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
            profileRepository.updateCurrentDoctor(data) { success ->
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
            profileRepository.updateCurrentDoctor(data) { success ->
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
                        sessionPrice = data["sessionPrice"] as? Double ?: _doctor.value!!.sessionPrice,
                        availability = (data["availability"] as? Map<String, List<TimeSlot>>)
                            ?: _doctor.value!!.availability
                    )
                }
                onDone(success)
            }
        }
    }

    // -------------------------------------------------------------
    // Delete Appointment
    // -------------------------------------------------------------
    fun deleteAppointment(appointmentId: String) {
        viewModelScope.launch {
            profileRepository.deleteAppointment(appointmentId) { success ->
                if (success) {
                    _appointments.value =
                        _appointments.value.filter { it.id != appointmentId }
                }
            }
        }
    }
}