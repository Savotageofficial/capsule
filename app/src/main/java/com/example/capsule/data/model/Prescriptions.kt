package com.example.capsule.data.model


data class Prescription(
    val id: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val date: Long = System.currentTimeMillis(),
    val medications: Map<String, Medication> = emptyMap(),
    val notes: String = "",
    val qrCodeUrl: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "patientId" to patientId,
            "patientName" to patientName,
            "doctorId" to doctorId,
            "doctorName" to doctorName,
            "date" to date,
            "medications" to medications.mapValues { it.value.toMap() },
            "notes" to notes,
            "qrCodeUrl" to qrCodeUrl
        )
    }
}

// Also add to Medication.kt (if not already present):
data class Medication(
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val duration: String = "",
    val instructions: String = ""
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "name" to name,
            "dosage" to dosage,
            "frequency" to frequency,
            "duration" to duration,
            "instructions" to instructions
        )
    }
}