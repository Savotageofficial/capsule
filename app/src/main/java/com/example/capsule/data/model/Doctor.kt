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
    val ratedByUsers: List<String> = emptyList(),
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
        get() = "EGP ${sessionPrice.toInt()}" // Or use currency formatting


}

data class TimeSlot(
    val start: String = "",
    val end: String = ""
) {
    // Helper methods for TimeSlot
    fun toDisplayString(): String = "$start - $end"
}