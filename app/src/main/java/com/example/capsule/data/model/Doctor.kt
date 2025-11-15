package com.example.capsule.data.model

data class Doctor(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val userType: String = "Doctor",
    val specialty: String = "",
    val bio: String = "",
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val experience: String = "",
    val clinicName: String = "",
    val clinicAddress: String = "",
    val locationUrl: String = "",
    val availability: String = "",
    val profileImageRes: Int? = null
) : UserProfile(id = id, name = name, email = email, userType = userType)
