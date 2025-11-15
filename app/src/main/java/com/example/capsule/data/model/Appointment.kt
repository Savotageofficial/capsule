package com.example.capsule.data.model

data class Appointment(
    val id: String = "",                 // unique id (useful for Firestore)
    val doctorName: String = "",         // for patient
    val patientName: String = "",        // for doctor
    val patientId: String = "",          // for doctor
    val time: String = "",               // for both
    val type: String = "",               // Chat , In-Person -> for both
    val status: String = "",             // Upcoming , Completed , Cancelled -> for patient
)
//ignore (by safwat)