package com.example.capsule.data.repository

import android.util.Log
import com.example.capsule.data.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchRepository {

    private val db = FirebaseFirestore.getInstance()
    fun getDoctorByName(name: String, speciality: String, callback: (List<Doctor>) -> Unit) {
        db.collection("doctors")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val doctorList = mutableListOf<Doctor>()

                for (document in querySnapshot.documents) {
                    try {
                        // Convert the entire document to Doctor object
                        val doctor = document.toObject(Doctor::class.java)?.copy(id = document.id)

                        if (doctor != null) {
                            val nameMatch = name.isEmpty() ||
                                    doctor.name.contains(name, ignoreCase = true)
                            val specialtyMatch = speciality.isEmpty() ||
                                    doctor.specialty.contains(speciality, ignoreCase = true)

                            if (nameMatch && specialtyMatch) {
                                // Ensure profile image is included
                                val finalDoctor = doctor.copy(
                                    id = document.id,
                                    profileImageBase64 = doctor.profileImageBase64
                                )
                                doctorList.add(finalDoctor)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SearchRepository", "Error parsing doctor: ${e.message}")
                    }
                }
                callback(doctorList)
            }
            .addOnFailureListener {
                Log.e("SearchRepository", "Error fetching doctors: ${it.message}")
                callback(emptyList())
            }
    }
}