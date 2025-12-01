package com.example.capsule.util

import com.example.capsule.data.model.TimeSlot
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.*

// ---------------- DATE FORMATTERS ----------------

private val dobFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val timeFormatter12 = DateTimeFormatter.ofPattern("h:mm a", Locale.US)

// ---------------- DATE HELPERS ----------------

fun formatDateOfBirth(dob: Long): String = runCatching {
    Instant.ofEpochMilli(dob)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(dobFormatter)
}.getOrElse { "Not set" }

fun getDayNameFromTimestamp(timestamp: Long): String =
    Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .dayOfWeek
        .name
        .lowercase()
        .replaceFirstChar { it.uppercase() }   // Converts "monday" → "Monday"

// Clean start/end of day
fun getStartOfDay(timestamp: Long): Long =
    Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

fun getEndOfDay(timestamp: Long): Long =
    Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atTime(23, 59, 59)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

// ---------------- TIME HELPERS ----------------

// Parse time safely from either "HH:mm" or "h:mm a"
private fun parseTimeSafe(timeStr: String): LocalTime =
    runCatching { LocalTime.parse(timeStr) }.getOrElse {
        LocalTime.parse(timeStr, timeFormatter12)
    }

fun convertDateTimeToMillis(date: LocalDate, timeStr: String): Long = runCatching {
    val time = parseTimeSafe(timeStr)
    date.atTime(time)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}.getOrElse {
    // fallback -> 9:00 AM
    date.atTime(9, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

// ---------------- AVAILABILITY HELPERS ----------------

fun formatAvailabilityForDisplay(map: Map<String, List<TimeSlot>>): String {
    val text = map.entries
        .filter { it.value.isNotEmpty() }
        .joinToString("\n") { (day, slots) ->
            "$day: ${slots.joinToString(", ") { "${it.start} - ${it.end}" }}"
        }

    return text.ifBlank { "Set Availability" }
}

fun formatAppointmentDateTime(timestamp: Long, slot: TimeSlot): String {
    val dateTime = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    return "${dateTime.format(dateFormatter)} at ${slot.start} - ${slot.end}"
}


fun formatChatTime(rawDate: String): String {
    return try {
        val date = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(rawDate)

        if (date != null) {
            val now = Date()
            val diff = now.time - date.time

            val oneDay = 24 * 60 * 60 * 1000

            return when {
                diff < oneDay -> {
                    // Today
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date)
                }
                diff < 2 * oneDay -> {
                    // Yesterday
                    "Yesterday • " + SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date)
                }
                else -> {
                    // Older dates
                    SimpleDateFormat("MMM dd • hh:mm a", Locale.ENGLISH).format(date)
                }
            }
        }

        rawDate
    } catch (_: Exception) {
        rawDate
    }
}
