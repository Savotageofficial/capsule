package com.example.capsule.model


data class Message(
    val senderId: String = "",
    val receivedId: String = "",
    val message: String = "",
    val timeStamp: Long = System.currentTimeMillis()
)
