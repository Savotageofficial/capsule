package com.example.capsule.data.model

import com.example.capsule.util.formatAppointmentDateTime

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

// Add to Appointment.kt
val Appointment.formattedDateTime: String
    get() = formatAppointmentDateTime(dateTime, timeSlot)

val Appointment.isUpcoming: Boolean
    get() = status == "Upcoming"

val Appointment.isCompleted: Boolean
    get() = status == "Completed"

val Appointment.isCancelled: Boolean
    get() = status == "Cancelled"