package com.example.capsule.data.repository

import android.util.Log
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun createAccount(
        email: String,
        password: String,
        name: String,
        userType: String,
        specialization: String? = null,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        msgHistory : List<String> = listOf<String>()
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = hashMapOf<String, Any?>(
                            "name" to name,
                            "email" to email,
                            "userType" to userType,
                            "specialization" to specialization,
                            "msgHistory" to msgHistory
                        )

                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                // Create profile document based on user type
                                if (userType == "Doctor") {
                                    createDoctorProfile(user.uid, name, email, specialization)
                                } else {
                                    createPatientProfile(user.uid, name, email)
                                }
                                sendEmailVerification(onSuccess, onFailure)
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception.message ?: "Failed to save user data")
                            }
                    } else {
                        onFailure("Failed to create user")
                    }
                } else {
                    onFailure(task.exception?.message ?: "Sign up failed")
                }
            }
    }

    private fun createDoctorProfile(
        uid: String,
        name: String,
        email: String,
        specialization: String?
    ) {
        val doctor = Doctor(
            id = uid,
            name = name,
            email = email,
            userType = "Doctor",
            specialty = specialization ?: "General Practitioner",
            bio = "Hello! I'm a dedicated healthcare professional.",
            rating = 0.0,
            reviewsCount = 0,
            experience = "0 years",
            clinicName = "My Clinic",
            clinicAddress = "Address not set",
            locationUrl = "",
            sessionPrice = 0.0,
            availability = emptyMap()
        )

        ProfileRepository.getInstance().createDoctor(doctor) { success ->
            if (success) {
                Log.d("AuthRepo", "Doctor profile created successfully for: $uid")
            } else {
                Log.e("AuthRepo", "Failed to create doctor profile for: $uid")
            }
        }
    }

    private fun createPatientProfile(uid: String, name: String, email: String) {
        val patient = Patient(
            id = uid,
            name = name,
            email = email,
            userType = "Patient",
            dob = 0,
            gender = "Not set",
            contact = "Not set"
        )

        ProfileRepository.getInstance().createPatient(patient) { success ->
            if (success) {
                Log.d("AuthRepo", "Patient profile created successfully for: $uid")
            } else {
                Log.e("AuthRepo", "Failed to create patient profile for: $uid")
            }
        }
    }

    // Email verification
    private fun sendEmailVerification(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUser = auth.currentUser
        currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure("Failed to send verification email")
                }
            } ?: onFailure("User not available")
    }

    // Sign in
    fun signIn(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser?.isEmailVerified == true) {
                        db.collection("users").document(currentUser.uid).get()
                            .addOnSuccessListener { document ->
                                val userType = document.getString("userType") ?: "Patient"
                                onSuccess(userType)
                            }
                            .addOnFailureListener {
                                onFailure("Couldn't get your info")
                            }
                    } else {
                        onFailure("Please verify your email")
                    }
                } else {
                    onFailure(task.exception?.message ?: "Sign in failed")
                }
            }
    }

    // Reset password
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Failed to send reset password email")
                }
            }
    }

    // Check if user signed in
    fun isUserSignedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null && currentUser.isEmailVerified
    }

    // Get current user type
    fun getCurrentUserType(onResult: (String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    onResult(document.getString("userType"))
                }
                .addOnFailureListener {
                    onResult(null)
                }
        } else {
            onResult(null)
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }
}