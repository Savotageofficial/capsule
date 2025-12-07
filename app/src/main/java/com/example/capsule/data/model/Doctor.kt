package com.example.capsule.data.model

import com.example.capsule.util.formatAvailabilityForDisplay

data class Doctor(
    override var id: String = "",
    override var name: String = "",
    override var email: String = "",
    override var userType: String = "Doctor",

    var specialty: String = "",
    val bio: String = "",
    val rating: Double = 0.0,
    val totalRating: Double = 0.0,
    val reviewsCount: Int = 0,
    val experience: String = "",
    val clinicName: String = "",
    val clinicAddress: String = "",
    val locationUrl: String = "",
    override val msgHistory : List<String> = listOf<String>(),
    val sessionPrice: Double = 0.0,

    // Store real availability as: { "Monday": [ {start,end}, {start,end} ], ... }
    val availability: Map<String, List<TimeSlot>> = emptyMap(),

    val profileImageRes: Int? = null
) : UserProfile(id = id, name = name, email = email, userType = userType , msgHistory = msgHistory) {

    // Formatted availability string for display
    val availabilityDisplay: String
        get() = formatAvailabilityForDisplay(availability)

    // Formatted session price for display
    val formattedSessionPrice: String
        get() = "$${sessionPrice.toInt()}" // Or use currency formatting

    // Check if doctor can prescribe to a patient (based on completed appointments)
    fun canPrescribeTo(patientId: String, appointments: List<Appointment>): Boolean {
        return appointments.any {
            it.patientId == patientId &&
                    it.doctorId == this.id &&
                    it.status == "Completed"
        }
    }

    // Get patients who have completed appointments with this doctor
    fun getPatientsWithCompletedAppointments(appointments: List<Appointment>): List<Appointment> {
        return appointments.filter {
            it.doctorId == this.id &&
                    it.status == "Completed"
        }.distinctBy { it.patientId }
    }

    // Get prescription stats (if you want to show in doctor profile)
    val prescriptionStats: Map<String, Any>?
        get() = null // This can be populated from Firestore if needed

    // Convenience method to check if doctor has any availability
    val hasAvailability: Boolean
        get() = availability.isNotEmpty()

    // Get next available time slot (for display purposes)
    val nextAvailableTime: String?
        get() {
            if (availability.isEmpty()) return null

            // Simple implementation - returns first time slot of the first day
            val firstDay = availability.entries.firstOrNull()
            return firstDay?.value?.firstOrNull()?.start
        }
}

data class TimeSlot(
    val start: String = "",
    val end: String = ""
) {
    // Helper methods for TimeSlot
    fun toDisplayString(): String = "$start - $end"

    fun isValid(): Boolean = start.isNotBlank() && end.isNotBlank()

    companion object {
        fun fromString(timeString: String): TimeSlot? {
            val parts = timeString.split("-")
            return if (parts.size == 2) {
                TimeSlot(start = parts[0].trim(), end = parts[1].trim())
            } else {
                null
            }
        }
    }
}