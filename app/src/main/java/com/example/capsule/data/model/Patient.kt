package com.example.capsule.data.model

data class Patient(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val userType: String = "Patient",

    val dob: String = "",
    val gender: String = "",
    val contact: String = "",
    val profileImageRes: Int? = null
) : UserProfile(id = id, name = name, email = email, userType = userType)
