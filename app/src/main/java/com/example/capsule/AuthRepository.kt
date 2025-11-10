package com.example.capsule

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
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf<String, Any?>(
                        "name" to name,
                        "email" to email,
                        "userType" to userType,
                        "specialization" to specialization
                    )
                    db.collection("users").document(user!!.uid).set(userData)
                        .addOnSuccessListener {
                            verifyEmail(onSuccess, onFailure)
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception.message ?: "Failed to save user data")
                        }
                } else {
                    onFailure(task.exception?.message ?: "Sign up failed")
                }
            }
    }

    private fun verifyEmail(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure("Failed to send verification email")
                }
            }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        db.collection("users").document(user.uid).get()
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

    fun isUserSignedIn(): Boolean {
        val user = auth.currentUser
        return user != null && user.isEmailVerified
    }

    fun getCurrentUserType(onResult: (String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
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
}

// لول فشخ تمب سيمب عاش اتنشن