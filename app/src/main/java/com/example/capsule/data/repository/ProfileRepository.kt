package com.example.capsule.data.repository

import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Medication
import com.example.capsule.data.model.Patient
import com.example.capsule.data.model.Prescription
import com.example.capsule.data.model.TimeSlot
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.collections.emptyList
import kotlin.collections.sortedBy

class ProfileRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()  // For UID

    private fun parseAppointmentFromDocument(doc: DocumentSnapshot): Appointment? {
        val data = doc.data ?: return null

        return try {
            // Parse timeSlot
            val timeSlotData = data["timeSlot"] as? Map<String, String>
            val timeSlot = if (timeSlotData != null) {
                TimeSlot(
                    start = timeSlotData["start"] ?: "",
                    end = timeSlotData["end"] ?: ""
                )
            } else {
                TimeSlot()
            }

            // Parse appointment with profile images
            Appointment(
                id = doc.id,
                doctorId = data["doctorId"] as? String ?: "",
                patientId = data["patientId"] as? String ?: "",
                doctorName = data["doctorName"] as? String ?: "",
                patientName = data["patientName"] as? String ?: "",
                dateTime = (data["dateTime"] as? Long) ?: 0L,
                timeSlot = timeSlot,
                type = data["type"] as? String ?: "",
                status = data["status"] as? String ?: "",
                doctorProfileImage = data["doctorProfileImage"] as? String,  // This should be stored
                patientProfileImage = data["patientProfileImage"] as? String  // This should be stored
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // CREATE PROFILES with Firebase ID
    fun createDoctor(doctor: Doctor, onDone: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Use Firebase Auth UID as document ID
            val doctorWithId = doctor.copy(id = currentUser.uid)
            db.collection("doctors")
                .document(currentUser.uid)
                .set(doctorWithId)
                .addOnSuccessListener { onDone(true) }
                .addOnFailureListener { onDone(false) }
        } else {
            onDone(false)
        }
    }

    fun createPatient(patient: Patient, onDone: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Use Firebase Auth UID as document ID
            val patientWithId = patient.copy(id = currentUser.uid)
            db.collection("patients")
                .document(currentUser.uid)
                .set(patientWithId)
                .addOnSuccessListener { onDone(true) }
                .addOnFailureListener { onDone(false) }
        } else {
            onDone(false)
        }
    }

    // GET PROFILES
    fun getCurrentDoctor(onResult: (Doctor?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("doctors")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { snap ->
                    onResult(snap.toObject(Doctor::class.java))  // converts the Firestore data into a Doctor object.
                }
                .addOnFailureListener { onResult(null) }
        } else {
            onResult(null)
        }
    }

    fun getCurrentPatient(onResult: (Patient?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("patients")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { snap ->
                    onResult(snap.toObject(Patient::class.java))
                }
                .addOnFailureListener { onResult(null) }
        } else {
            onResult(null)
        }
    }

    // Get by specific ID (for viewing other profiles)
    fun getDoctorById(id: String, onResult: (Doctor?) -> Unit) {
        db.collection("doctors")
            .document(id)
            .get()
            .addOnSuccessListener { snap ->
                onResult(snap.toObject(Doctor::class.java))
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getPatientById(id: String, onResult: (Patient?) -> Unit) {
        db.collection("patients")
            .document(id)
            .get()
            .addOnSuccessListener { snap ->
                onResult(snap.toObject(Patient::class.java))
            }
            .addOnFailureListener { onResult(null) }
    }

    //     UPDATE  PROFILES
    fun updateCurrentDoctor(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("doctors")
                .document(currentUser.uid)
                .update(data)
                .addOnSuccessListener { onDone(true) }
                .addOnFailureListener { onDone(false) }
        } else {
            onDone(false)
        }
    }

    fun updateCurrentPatient(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("patients")
                .document(currentUser.uid)
                .update(data)
                .addOnSuccessListener { onDone(true) }
                .addOnFailureListener { onDone(false) }
        } else {
            onDone(false)
        }
    }

    // if you wanna add it
    fun deleteAppointment(appointmentId: String, onDone: (Boolean) -> Unit) {
        db.collection("appointments")
            .document(appointmentId)
            .delete()
            .addOnSuccessListener { onDone(true) }
            .addOnFailureListener { onDone(false) }
    }

    // Book appointment with conflict check
    // In ProfileRepository.kt
    fun bookAppointment(appointment: Appointment, onDone: (Boolean) -> Unit) {

        val appointmentData = mapOf(
            "doctorId" to appointment.doctorId,
            "patientId" to appointment.patientId,
            "doctorName" to appointment.doctorName,
            "patientName" to appointment.patientName,
            "dateTime" to appointment.dateTime,
            "timeSlot" to mapOf(
                "start" to appointment.timeSlot.start,
                "end" to appointment.timeSlot.end
            ),
            "type" to appointment.type,
            "status" to appointment.status,
            "doctorProfileImage" to (appointment.doctorProfileImage ?: ""),
            "patientProfileImage" to (appointment.patientProfileImage ?: ""),
            "createdAt" to Timestamp.now()
        )

        db.collection("appointments")
            .add(appointmentData)
            .addOnSuccessListener {
                onDone(true)
                // Send notification message
                db.collection("messages").add(
                    mapOf(
                        "message" to "${appointment.patientName} has reserved an appointment, please check your schedule",
                        "senderId" to appointment.patientId,
                        "timestamp" to Timestamp.now(),
                        "receiverId" to appointment.doctorId
                    )
                )
            }
            .addOnFailureListener {
                onDone(false)
            }
    }


    fun updateAppointmentStatus(appointmentId: String, status: String, onDone: (Boolean) -> Unit) {
        db.collection("appointments")
            .document(appointmentId)
            .update("status", status)
            .addOnSuccessListener { onDone(true) }
            .addOnFailureListener { onDone(false) }
    }

    fun getPatientAppointments(patientId: String, onResult: (List<Appointment>) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("patientId", patientId)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val appointments = querySnapshot?.documents?.mapNotNull { doc ->
                    parseAppointmentFromDocument(doc)
                }?.sortedBy { it.dateTime } ?: emptyList()

                onResult(appointments)
            }
    }

    fun getDoctorAppointments(doctorId: String, onResult: (List<Appointment>) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val appointments = querySnapshot?.documents?.mapNotNull { doc ->
                    parseAppointmentFromDocument(doc)
                } ?: emptyList()

                onResult(appointments)
            }
    }

    // PRESCRIPTION CRUD OPERATIONS
    fun createPrescription(prescription: Prescription, onDone: (Boolean, String?) -> Unit) {
        db.collection("prescriptions")
            .add(prescription.toMap())
            .addOnSuccessListener { documentReference ->
                onDone(true, documentReference.id)
            }
            .addOnFailureListener {
                onDone(false, null)
            }
    }

    fun getPrescriptionsByPatient(patientId: String, onResult: (List<Prescription>) -> Unit) {
        db.collection("prescriptions")
            .whereEqualTo("patientId", patientId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val prescriptions = querySnapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        val medsMap =
                            (data["medications"] as? Map<String, Map<String, Any>>)?.mapValues { entry ->
                                Medication(
                                    name = entry.value["name"] as? String ?: "",
                                    dosage = entry.value["dosage"] as? String ?: "",
                                    frequency = entry.value["frequency"] as? String ?: "",
                                    duration = entry.value["duration"] as? String ?: "",
                                    instructions = entry.value["instructions"] as? String ?: ""
                                )
                            } ?: emptyMap()

                        Prescription(
                            id = doc.id,
                            patientId = data["patientId"] as? String ?: "",
                            patientName = data["patientName"] as? String ?: "",
                            doctorId = data["doctorId"] as? String ?: "",
                            doctorName = data["doctorName"] as? String ?: "",
                            date = (data["date"] as? Long) ?: (data["date"] as? Double)?.toLong()
                            ?: 0L,
                            medications = medsMap,
                            notes = data["notes"] as? String ?: "",
                            qrCodeUrl = data["qrCodeUrl"] as? String ?: ""
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                } ?: emptyList()

                onResult(prescriptions)
            }
    }

    fun getPrescriptionsByDoctor(doctorId: String, onResult: (List<Prescription>) -> Unit) {
        db.collection("prescriptions")
            .whereEqualTo("doctorId", doctorId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val prescriptions = mutableListOf<Prescription>()
                querySnapshot?.documents?.forEach { doc ->
                    val data = doc.data ?: return@forEach
                    try {
                        val medsMap =
                            (data["medications"] as? Map<String, Map<String, Any>>)?.mapValues { entry ->
                                Medication(
                                    name = entry.value["name"] as? String ?: "",
                                    dosage = entry.value["dosage"] as? String ?: "",
                                    frequency = entry.value["frequency"] as? String ?: "",
                                    duration = entry.value["duration"] as? String ?: "",
                                    instructions = entry.value["instructions"] as? String ?: ""
                                )
                            } ?: emptyMap()

                        val prescription = Prescription(
                            id = doc.id,
                            patientId = data["patientId"] as? String ?: "",
                            patientName = data["patientName"] as? String ?: "",
                            doctorId = data["doctorId"] as? String ?: "",
                            doctorName = data["doctorName"] as? String ?: "",
                            date = (data["date"] as? Long) ?: (data["date"] as? Double)?.toLong()
                            ?: 0L,
                            medications = medsMap,
                            notes = data["notes"] as? String ?: "",
                            qrCodeUrl = data["qrCodeUrl"] as? String ?: ""
                        )
                        prescriptions.add(prescription)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                onResult(prescriptions)
            }
    }

    fun deletePrescription(prescriptionId: String, onDone: (Boolean) -> Unit) {
        db.collection("prescriptions")
            .document(prescriptionId)
            .delete()
            .addOnSuccessListener { onDone(true) }
            .addOnFailureListener { onDone(false) }
    }

    fun getPrescriptionById(prescriptionId: String, onResult: (Prescription?) -> Unit) {
        db.collection("prescriptions")
            .document(prescriptionId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data
                    val prescription = Prescription(
                        id = document.id,
                        patientId = data?.get("patientId") as? String ?: "",
                        patientName = data?.get("patientName") as? String ?: "",
                        doctorId = data?.get("doctorId") as? String ?: "",
                        doctorName = data?.get("doctorName") as? String ?: "",
                        date = (data?.get("date") as? Long)
                            ?: (data?.get("date") as? Double)?.toLong() ?: 0L,
                        medications = parseMedications(data?.get("medications")),
                        notes = data?.get("notes") as? String ?: "",
                        qrCodeUrl = data?.get("qrCodeUrl") as? String ?: ""
                    )
                    onResult(prescription)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    private fun parseMedications(medsData: Any?): Map<String, Medication> {
        return try {
            val medsMap = mutableMapOf<String, Medication>()
            (medsData as? Map<String, Map<String, Any>>)?.forEach { (key, value) ->
                medsMap[key] = Medication(
                    name = value["name"] as? String ?: "",
                    dosage = value["dosage"] as? String ?: "",
                    frequency = value["frequency"] as? String ?: "",
                    duration = value["duration"] as? String ?: "",
                    instructions = value["instructions"] as? String ?: ""
                )
            }
            medsMap
        } catch (_: Exception) {
            emptyMap()
        }
    }


    fun rateDoctor(doctorId: String, patientId: String, rating: Int, onDone: (Boolean) -> Unit) {
        val doctorRef = db.collection("doctors").document(doctorId)

        db.runTransaction { transaction ->
            val doctorDoc = transaction.get(doctorRef)
            val doctor = doctorDoc.toObject(Doctor::class.java) ?: return@runTransaction null

            // Check if patient already rated
            if (doctor.ratedByUsers.contains(patientId)) {
                return@runTransaction false
            }

            // Calculate new rating
            val newTotalRating = doctor.totalRating + rating
            val newReviewsCount = doctor.reviewsCount + 1
            val newRating = newTotalRating / newReviewsCount.toDouble()

            // Update ratedByUsers list
            val updatedRatedByUsers = doctor.ratedByUsers + patientId

            // Update the doctor document
            transaction.update(
                doctorRef, mapOf(
                    "totalRating" to newTotalRating,
                    "reviewsCount" to newReviewsCount,
                    "rating" to newRating,
                    "ratedByUsers" to updatedRatedByUsers
                )
            )

            return@runTransaction true
        }.addOnSuccessListener { success ->
            onDone(success == true)
        }.addOnFailureListener {
            onDone(false)
        }
    }

    fun hasUserRatedDoctor(doctorId: String, userId: String, onResult: (Boolean) -> Unit) {
        db.collection("doctors")
            .document(doctorId)
            .get()
            .addOnSuccessListener { document ->
                val doctor = document.toObject(Doctor::class.java)
                val hasRated = doctor?.ratedByUsers?.contains(userId) ?: false
                onResult(hasRated)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    // Upload profile image
    fun uploadProfileImage(
        userId: String,
        userType: String,
        base64Image: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val collection = when (userType) {
            "Doctor" -> "doctors"
            "Patient" -> "patients"
            else -> return onFailure("Invalid user type")
        }

        db.collection(collection)
            .document(userId)
            .update("profileImageBase64", base64Image)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to upload image")
            }
    }

    // Delete profile image
    fun deleteProfileImage(
        userId: String,
        userType: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val collection = when (userType) {
            "Doctor" -> "doctors"
            "Patient" -> "patients"
            else -> return onFailure("Invalid user type")
        }

        db.collection(collection)
            .document(userId)
            .update("profileImageBase64", null)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to delete image")
            }
    }

    fun getDoctorProfileImage(doctorId: String, onResult: (String?) -> Unit) {
        db.collection("doctors")
            .document(doctorId)
            .get()
            .addOnSuccessListener { document ->
                val doctor = document.toObject(Doctor::class.java)
                onResult(doctor?.profileImageBase64)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getPatientProfileImage(patientId: String, onResult: (String?) -> Unit) {
        db.collection("patients")
            .document(patientId)
            .get()
            .addOnSuccessListener { document ->
                val patient = document.toObject(Patient::class.java)
                onResult(patient?.profileImageBase64)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


//    private fun parseAppointmentFromDocument(doc: DocumentSnapshot): Appointment? {
//        val data = doc.data ?: return null
//
//        return try {
//            val timeSlotData = data["timeSlot"] as? Map<String, String>
//            val timeSlot = if (timeSlotData != null) {
//                TimeSlot(
//                    start = timeSlotData["start"] ?: "",
//                    end = timeSlotData["end"] ?: ""
//                )
//            } else {
//                TimeSlot()
//            }
//            Appointment(
//                id = doc.id,
//                doctorId = data["doctorId"] as? String ?: "",
//                patientId = data["patientId"] as? String ?: "",
//                doctorName = data["doctorName"] as? String ?: "",
//                patientName = data["patientName"] as? String ?: "",
//                dateTime = (data["dateTime"] as? Long) ?: 0L,
//                timeSlot = timeSlot,
//                type = data["type"] as? String ?: "",
//                status = data["status"] as? String ?: "",
//                doctorProfileImage = data["doctorProfileImage"] as? String,
//                patientProfileImage = data["patientProfileImage"] as? String
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

    companion object {
        // Singleton instance
        private var instance: ProfileRepository? = null

        fun getInstance(): ProfileRepository {
            return instance ?: synchronized(this) {
                instance ?: ProfileRepository().also { instance = it } // create ProfileRepository
            }
        }
    }
}