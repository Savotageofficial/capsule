package com.example.capsule.model

data class Doctor(
    val name: String = "",
    val specialty: String = "",
    val bio: String = "",
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val licenseNumber: String = "",
    val experience: String = "",
    val clinicName: String = "",
    val clinicAddress: String = "",
    val availability: String = "",
    val profileImageRes: Int? = null
)
