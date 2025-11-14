package com.example.capsule.model

data class Patient(
    val id: String,         // for Firebase (unique hashcode)
    val name: String,
    val dob: String,
    val gender: String,
    val contact: String,
    val profileImageRes: Int? = null,
    val email: String,
)

//ignore (by safwat)