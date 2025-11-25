package com.example.capsule.util

import com.example.capsule.data.model.TimeSlot
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateOfBirth(dob: Long): String {
    return try {
        val date = Instant.ofEpochMilli(dob)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        date.format(formatter)
    } catch (_: Exception) {
        "Not set"
    }
}

fun formatAvailabilityForDisplay(map: Map<String, List<TimeSlot>>): String {
    val builder = StringBuilder()

    map.forEach { (day, slots) ->
        if (slots.isNotEmpty()) {
            builder.append("$day: ${slots.joinToString(", ")}\n")
        }
    }

    return if (builder.isEmpty()) "Set Availability" else builder.toString().trim()
}

fun convertDateTimeToMillis(date: LocalDate, time: String): Long {
    val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
    val parsedTime = LocalTime.parse(time, formatter)
    val dateTime = LocalDateTime.of(date, parsedTime)
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
