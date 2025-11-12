package com.example.capsule.ui.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.capsule.R
import com.example.capsule.model.Appointment
import com.example.capsule.model.Doctor

class DoctorProfileViewModel : ViewModel() {

    private val _doctor = mutableStateOf(
        Doctor(
            name = "Dr. Evelyn Reed",
            specialty = "Cardiologist",
            bio = "Dedicated cardiologist with a passion for preventative care and patient education.",
            rating = 4.9,
            reviewsCount = 125,
            experience = "15+ years of experience",
            clinicName = "City Heart Center",
            clinicAddress = "123 Main St, Anytown",
            locationUrl = "https://maps.app.goo.gl/W65gPob13KHBY3fCA",
            availability = "Mon, Wed, Fri | 9:00 AM - 5:00 PM",
            profileImageRes = R.drawable.doctor_avatar
        )

    )
    val upcomingAppointments = listOf(
        Appointment(
            doctorName = "Dr. Evelyn Reed",
            patientName = "Liam Johnson",
            time = "10:00 AM",
            type = "Video Call"
        ),
        Appointment(
            doctorName = "Dr. Evelyn Reed",
            patientName = "Olivia Williams",
            time = "11:30 AM",
            type = "In-Person"
        ),
        Appointment(
            doctorName = "Dr. Evelyn Reed",
            patientName = "Noah Davis",
            time = "2:00 PM",
            type = "Checkup"
        )
    )

    val doctor: State<Doctor> = _doctor
}