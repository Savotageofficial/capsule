package com.example.capsule.data.repository

import com.example.capsule.data.model.Appointment
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()  // For UID

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

    //     UPDATE PROFILES
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

    fun getDoctorAppointments(doctorId: String, onResult: (List<Appointment>) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .whereEqualTo("status", "Upcoming")
            .get()
            .addOnSuccessListener { querySnapshot -> // the documents from firebase
                val appointments = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Appointment::class.java)?.copy(id = doc.id)
                }
                onResult(appointments)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
    fun deleteAppointment(appointmentId: String, onDone: (Boolean) -> Unit) {
        db.collection("appointments")
            .document(appointmentId)
            .delete()
            .addOnSuccessListener { onDone(true) }
            .addOnFailureListener { onDone(false) }
    }
    fun bookAppointment(appointment: Appointment, onDone: (Boolean) -> Unit) {
        db.collection("appointments")
            .add(appointment)
            .addOnSuccessListener {
                // Update the appointment with the generated ID
                db.collection("appointments")
                    .document(it.id)
                    .update("id", it.id)
                    .addOnSuccessListener { onDone(true) }
                    .addOnFailureListener { onDone(false) }
            }
            .addOnFailureListener { onDone(false) }
    }

    fun getPatientAppointments(patientId: String, onResult: (List<Appointment>) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("patientId", patientId)
            .whereEqualTo("status", "Upcoming")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val appointments = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Appointment::class.java)?.copy(id = doc.id)
                }.sortedBy { it.dateTime }
                onResult(appointments)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun updateAppointmentStatus(appointmentId: String, status: String, onDone: (Boolean) -> Unit) {
        db.collection("appointments")
            .document(appointmentId)
            .update("status", status)
            .addOnSuccessListener { onDone(true) }
            .addOnFailureListener { onDone(false) }
    }

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