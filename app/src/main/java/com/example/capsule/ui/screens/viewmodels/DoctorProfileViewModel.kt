package com.example.capsule.ui.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class DoctorProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository.getInstance()
) : ViewModel() {

    private val _doctor = mutableStateOf<Doctor?>(null)
    val doctor: State<Doctor?> = _doctor

    private val _appointments = mutableStateOf<List<Appointment>>(emptyList())
    val appointments: State<List<Appointment>> = _appointments              // booking complete

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Dummy appointments for development
    private val dummyAppointments = listOf(
        Appointment(
            id = "1",
            doctorName = "Dr. John Smith",
            patientName = "Sarah Johnson",
            patientId = "patient123",
            time = "10:00 AM",
            type = "In-Person",
            status = "Upcoming"
        ),
        Appointment(
            id = "2",
            doctorName = "Dr. John Smith",
            patientName = "Mike Wilson",
            patientId = "patient456",
            time = "2:30 PM",
            type = "Video Call",
            status = "Upcoming"
        ),
        Appointment(
            id = "3",
            doctorName = "Dr. John Smith",
            patientName = "Emma Davis",
            patientId = "patient789",
            time = "4:15 PM",
            type = "In-Person",
            status = "Upcoming"
        )
    )

    //   LOAD CURRENT DOCTOR PROFILE (using Firebase UID)
    fun loadCurrentDoctorProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getCurrentDoctor { result ->
                _doctor.value = result
                _isLoading.value = false
                // Load appointments after doctor data is loaded
                loadDoctorAppointments()
            }
        }
    }

    //   LOAD DOCTOR PROFILE BY ID (for viewing other profiles)
    fun loadDoctorProfileById(doctorId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getDoctorById(doctorId) { result ->
                _doctor.value = result
                _isLoading.value = false
                // Load appointments after doctor data is loaded
                loadDoctorAppointments()
            }
        }
    }

    //   UPDATE DOCTOR PROFILE
    fun updateDoctorProfile(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.updateCurrentDoctor(data) { success ->
                if (success) {
                    // update UI immediately
                    val old = _doctor.value
                    if (old != null) {
                        val updatedDoctor = old.copy(
                            name = data["name"] as? String ?: old.name,
                            specialty = data["specialty"] as? String ?: old.specialty,
                            bio = data["bio"] as? String ?: old.bio,
                            experience = data["experience"] as? String ?: old.experience,
                            clinicName = data["clinicName"] as? String ?: old.clinicName,
                            clinicAddress = data["clinicAddress"] as? String ?: old.clinicAddress,
                            locationUrl = data["locationUrl"] as? String ?: old.locationUrl,
                            availability = data["availability"] as? String ?: old.availability
                        )
                        _doctor.value = updatedDoctor
                    }
                }
                onDone(success)
            }
        }
    }

    //   LOAD APPOINTMENTS (using dummy data for now) - KEEP ONLY THIS ONE
    fun loadDoctorAppointments() {
        _isLoading.value = true
        // For now, use dummy data but assign to state
        _appointments.value = dummyAppointments
        _isLoading.value = false

        // TODO: Later connect to Firestore:
        // db.collection("appointments").whereEqualTo("doctorId", currentUserId).get()
        // where doctorId == current user ID
    }

    //   DELETE APPOINTMENT
    fun deleteAppointment(appointmentId: String) {
        val currentAppointments = _appointments.value.toMutableList()
        currentAppointments.removeAll { it.id == appointmentId }
        _appointments.value = currentAppointments

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