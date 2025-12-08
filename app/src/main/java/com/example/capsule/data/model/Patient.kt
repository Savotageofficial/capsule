package com.example.capsule.data.model

data class Patient(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val userType: String = "Patient",
    override val msgHistory: List<String> = listOf(),
    override val profileImageBase64: String? = null, // Add this

    val dob: Long = System.currentTimeMillis(),
    val gender: String = "",
    val contact: String = "",
) : UserProfile(id = id, name = name, email = email, userType = userType,
    msgHistory = msgHistory, profileImageBase64 = profileImageBase64)