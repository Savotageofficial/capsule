package com.example.capsule.data.model

open class UserProfile(
    open val id: String = "",
    open val name: String = "",
    open val email: String = "",
    open val userType: String = "",
    open val msgHistory: List<String> = listOf<String>(),
    open val profileImageBase64: String? = null
)