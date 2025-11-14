package com.example.capsule

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // create account
    fun createAccount(
        email: String,
        password: String,
        name: String,
        userType: String,
        specialization: String? = null,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val userData = hashMapOf<String, Any?>(
                            "name" to name,
                            "email" to email,
                            "userType" to userType,
                            "specialization" to specialization
                        )

                        db.collection("users").document(currentUser.uid)
                            .set(userData)
                            .addOnSuccessListener {
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

    // email verification
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

    // sign in
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
                                onFailure("Couldn’t get your info")
                            }
                    } else {
                        onFailure("Please verify your email")
                    }
                } else {
                    onFailure(task.exception?.message ?: "Sign in failed")
                }
            }
    }

    // reset password
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

    // check if user signed in
    fun isUserSignedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null && currentUser.isEmailVerified
    }


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

    fun logout() {
        auth.signOut()
    }
}
