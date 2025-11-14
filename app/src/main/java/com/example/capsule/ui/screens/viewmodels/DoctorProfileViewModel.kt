package com.example.capsule.ui.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.example.capsule.data.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DoctorProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository.getInstance()
) : ViewModel() {

    private val _doctor = mutableStateOf<Doctor?>(null)
    val doctor: State<Doctor?> = _doctor

    private val _appointments = mutableStateOf<List<Appointment>>(emptyList())
    val appointments: State<List<Appointment>> = _appointments

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Dummy appointments for development
    val upcomingAppointments = listOf(
        Appointment(
            id = "1",
            doctorName = "Dr. John Smith",
            patientName = "Sarah Johnson",
            time = "10:00 AM",
            type = "In-Person",
            status = "Upcoming"
        ),
        Appointment(
            id = "2",
            doctorName = "Dr. John Smith",
            patientName = "Mike Wilson",
            time = "2:30 PM",
            type = "Video Call",
            status = "Upcoming"
        ),
        Appointment(
            id = "3",
            doctorName = "Dr. John Smith",
            patientName = "Emma Davis",
            time = "4:15 PM",
            type = "In-Person",
            status = "Upcoming"
        ),
        Appointment(
            id = "4",
            doctorName = "Dr. John Smith",
            patientName = "Robert Brown",
            time = "11:30 AM",
            type = "Video Call",
            status = "Upcoming"
        ),
        Appointment(
            id = "5",
            doctorName = "Dr. John Smith",
            patientName = "Lisa Anderson",
            time = "3:45 PM",
            type = "In-Person",
            status = "Upcoming"
        )
    )

    // ADD THIS METHOD - Your screens are calling it!
    fun loadDoctorProfile(doctorId: String) {
        if (doctorId == "current" || doctorId.isEmpty()) {
            loadCurrentDoctorProfile()
        } else {
            loadDoctorProfileById(doctorId)
        }
    }

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

    //   CREATE DOCTOR PROFILE (for signup)
    fun createDoctorProfile(doctor: Doctor, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.createDoctor(doctor) { success ->
                if (success) {
                    _doctor.value = doctor.copy(id = FirebaseAuth.getInstance().currentUser?.uid ?: "")
                }
                onDone(success)
            }
        }
    }

    //   LOAD APPOINTMENTS (using dummy data for now)
    fun loadDoctorAppointments() {
        // You can later connect this to Firestore collection("appointments")
        // where doctorId == current user ID
        _appointments.value = upcomingAppointments
    }

    //   ADD NEW APPOINTMENT (for schedule management)
    fun addAppointment(appointment: Appointment) {
        val currentAppointments = _appointments.value.toMutableList()
        currentAppointments.add(appointment)
        _appointments.value = currentAppointments
    }

    //   DELETE APPOINTMENT
    fun deleteAppointment(appointmentId: String) {
        val currentAppointments = _appointments.value.toMutableList()
        currentAppointments.removeAll { it.id == appointmentId }
        _appointments.value = currentAppointments
    }

    //   GET TODAY'S APPOINTMENTS
    fun getTodaysAppointments(): List<Appointment> {
        return upcomingAppointments.take(3) // Return first 3 as today's appointments
    }

    //   GET APPOINTMENTS BY PATIENT NAME
    fun getAppointmentsByPatient(patientName: String): List<Appointment> {
        return upcomingAppointments.filter { it.patientName == patientName }
    }

    companion object {
        fun previewDoctor(): Doctor {
            return Doctor(
                id = "preview_doctor_123",
                name = "Dr. John Smith",
                email = "john.smith@hospital.com",
                userType = "Doctor",
                specialty = "Cardiologist",
                bio = "Experienced cardiologist with 10+ years of practice...",
                rating = 4.8,
                reviewsCount = 142,
                experience = "10 years",
                clinicName = "City Heart Center",
                clinicAddress = "123 Medical Ave, Healthcare City",
                locationUrl = "https://maps.google.com/?q=123+Medical+Ave",
                availability = "Mon-Fri: 9AM-5PM",
                profileImageRes = null // You can add a drawable resource ID here
            )
        }

        fun previewPatient(): Patient {
            return Patient(
                id = "preview_patient_123",
                name = "Sarah Johnson",
                email = "sarah.j@email.com",
                userType = "Patient",
                dob = "1990-05-15",
                gender = "Female",
                contact = "+1234567890",
                profileImageRes = null // You can add a drawable resource ID here
            )
        }

        // Additional dummy appointments for different scenarios
        fun getSampleAppointments(): List<Appointment> {
            return listOf(
                Appointment(
                    id = "1",
                    doctorName = "Dr. John Smith",
                    patientName = "Sarah Johnson",
                    time = "10:00 AM",
                    type = "In-Person",
                    status = "Upcoming"
                ),
                Appointment(
                    id = "2",
                    doctorName = "Dr. John Smith",
                    patientName = "Mike Wilson",
                    time = "2:30 PM",
                    type = "Video Call",
                    status = "Upcoming"
                ),
                Appointment(
                    id = "3",
                    doctorName = "Dr. John Smith",
                    patientName = "Emma Davis",
                    time = "4:15 PM",
                    type = "In-Person",
                    status = "Upcoming"
                )
            )
        }
    }
}