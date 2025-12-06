package com.example.capsule.data.model

data class Appointment(
    val id: String = "",
    val doctorId: String = "",
    val patientId: String = "",
    val doctorName: String = "",
    val patientName: String = "",
    val dateTime: Long = 0L, // timestamp
    val timeSlot: TimeSlot = TimeSlot(),
    val type: String = "", // "In-Person", "Chat"
    val status: String = "" // "Upcoming", "Completed", "Cancelled"
)

val Appointment.isUpcoming: Boolean
    get() = status == "Upcoming"

val Appointment.isCompleted: Boolean
    get() = status == "Completed"

val Appointment.isCancelled: Boolean
    get() = status == "Cancelled"