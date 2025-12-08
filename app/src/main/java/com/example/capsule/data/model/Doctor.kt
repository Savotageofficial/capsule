package com.example.capsule.data.model

import android.os.Parcelable
import com.example.capsule.util.formatAvailabilityForDisplay
import kotlinx.parcelize.Parcelize

@Parcelize
data class Doctor(
    override var id: String = "",
    override var name: String = "",
    override var email: String = "",
    override var userType: String = "Doctor",
    override var profileImageBase64: String? = null, // Add this

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
    override val msgHistory: List<String> = listOf(),
    val sessionPrice: Double = 0.0,
    val availability: Map<String, List<TimeSlot>> = emptyMap(),
) : UserProfile(
    id = id, name = name, email = email, userType = userType,
    msgHistory = msgHistory, profileImageBase64 = profileImageBase64
) {

    // Formatted availability string for display
    val availabilityDisplay: String
        get() = formatAvailabilityForDisplay(availability)

    // Formatted session price for display
    val formattedSessionPrice: String
        get() = "EGP ${sessionPrice.toInt()}"
}
@Parcelize
data class TimeSlot(
    val start: String = "",
    val end: String = ""
): Parcelable {
    fun toDisplayString(): String = "$start - $end"
}