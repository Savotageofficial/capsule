package com.example.capsule

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.capsule.model.Doctor

class DoctorProfileViewModel : ViewModel() {

    private val _doctor = mutableStateOf(
        Doctor(
            name = "Dr. Evelyn Reed",
            specialty = "Cardiologist",
            bio = "Dedicated cardiologist with a passion for preventative care and patient education.",
            rating = 4.9,
            reviewsCount = 125,
            licenseNumber = "123456",
            experience = "15+ years of experience",
            clinicName = "City Heart Center",
            clinicAddress = "123 Main St, Anytown",
            availability = "Mon, Wed, Fri | 9:00 AM - 5:00 PM",
            profileImageRes = R.drawable.doctor_avatar
        )
    )
    val doctor: State<Doctor> = _doctor
}
