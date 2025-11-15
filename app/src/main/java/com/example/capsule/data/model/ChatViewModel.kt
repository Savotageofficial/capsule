package com.example.capsule.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private var chatId: String? = null

    // Generate unique chat ID based on two user IDs
    fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) user1 + user2 else user2 + user1
    }

    // Start observing messages for this chat
    fun startObserving(user1: String, user2: String) {
        chatId = getChatId(user1, user2)

        db.collection("chats")
            .document(chatId!!)
            .collection("messages")
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)?.copy(receivedId = doc.id)
                    }
                    _messages.value = list
                }
            }
    }

    // Send message to this chat
    fun sendMessage(senderId: String, text: String) {
        val id = chatId ?: return // don’t send if chatId is not set

        viewModelScope.launch {
            if (text.isBlank()) return@launch

            val message = Message(
                senderId = senderId,
                message = text,
                timeStamp = System.currentTimeMillis()
            )

            db.collection("chats")
                .document(id)
                .collection("messages")
                .add(message)
        }
    }
}
