package com.example.capsule.model

data class Doctor(
    val id: String,         // for Firebase (unique hashcode)
    val name: String = "",
    val specialty: String = "",
    val bio: String = "",
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val experience: String = "",
    val clinicName: String = "",
    val clinicAddress: String = "",
    val locationUrl: String = "",
    val availability: String = "",
    val profileImageRes: Int? = null,
    val appointments: List<Appointment> = emptyList()
)
//ignore (by safwat)
