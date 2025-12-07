package com.example.capsule.data.model

data class Patient(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val userType: String = "Patient",
    override val msgHistory : List<String> = listOf(),

    val dob: Long = System.currentTimeMillis(),
    val gender: String = "",
    val contact: String = "",
    val profileImageRes: Int? = null
) : UserProfile(id = id, name = name, email = email, userType = userType , msgHistory = msgHistory)
