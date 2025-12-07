//package com.example.capsule.model
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.Query
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Message
import com.example.capsule.data.model.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


//data class Doctor(
//    val id: String,
//    val name: String,
//    val specialty: String
//)

class ChatHistoryViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors = _doctors.asStateFlow()
    private val _patients = MutableStateFlow<List<Patient>>(emptyList())

    val patient = _patients.asStateFlow()

    fun loadPatientChatHistory() {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("patients")
            .document(currentUid)
            .get()
            .addOnSuccessListener { patientDoc ->
                val historyIds = patientDoc.get("msgHistory") as? List<String> ?: emptyList()

                // fetch all doctors using coroutine
                viewModelScope.launch {
                    fetchDoctors(historyIds)
                }
            }
    }

    fun loadDoctorChatHistory(doctorId: String) {
        //.whereEqualTo("receiverId", doctorId)
        var messages: List<Message>

        var senderIds = mutableListOf<String>()



        db.collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e == null && snapshot != null) {
                    val fetchedMessages = snapshot.documents.map {
                        Message(
                            message = it.getString("message") ?: "",
                            senderId = it.getString("senderId") ?: "",
                            timestamp = it.getTimestamp("timestamp")?.toDate().toString(),
                            recieverId = it.getString("receiverId") ?: ""
                        )
                    }
                    Log.d("ChatDebug", "Received messages: $snapshot", e)
                    messages = fetchedMessages.filter {it.recieverId == doctorId}

                    for (message in messages){
                        if (!senderIds.contains(message.senderId)){
                            senderIds.add(message.senderId)
                        }
                        Log.d("ChatDebug", "Received messages: ${message.message}, senderId: ${message.senderId}")
                    }
                    viewModelScope.launch {
                        fetchPatients(senderIds)
                    }

                }
            }
    }

    private suspend fun fetchDoctors(ids: List<String>) {
        val doctorList = mutableListOf<Doctor>()

        ids.forEach { id ->
            val docSnapshot = db.collection("doctors")
                .document(id)
                .get()
                .await()

            val doctor = Doctor(
                id = docSnapshot.id,
                name = docSnapshot.getString("name") ?: "error fetching name", //added error fetching name if name returned null, hamza's super code!
                specialty = docSnapshot.getString("specialty") ?: ""
            )
            doctorList.add(doctor)
        }

        _doctors.value = doctorList
    }

    private suspend fun fetchPatients(ids: List<String>) {
        val patientList = mutableListOf<Patient>()


        ids.forEach { id ->
            val docSnapshot = db.collection("patients")
                .document(id).get().await()

            val patient = Patient(
                id = docSnapshot.id,
                name = docSnapshot.getString("name") ?: "error fetching name",
                //speciality = shit Blood when i Fart
            )
            patientList.add(patient)
            Log.d("ChatDebug", "list: $patientList")
        }

        _patients.value = patientList
    }//would be faster to fetch all and compare all, but i am too lazy, hey it works!
}


//class ChatViewModel(private val prefs: Prefs) : ViewModel() {
//
//    // StateFlow holds the current value and updates UI automatically
//    private val _userName = MutableStateFlow(prefs.getName())
//    val userName: StateFlow<String> = _userName
//
//    fun updateName(name: String) {
//        prefs.saveName(name)       // save to SharedPreferences
//        _userName.value = name     // update StateFlow so UI refreshes
//    }
//}



//class ChatViewModel : ViewModel() {
//
//    private val db = FirebaseFirestore.getInstance()
//    private val _messages = MutableStateFlow<List<Message>>(emptyList())
//    val messages: StateFlow<List<Message>> = _messages
//
//    private var chatId: String? = null
//
//    // Generate unique chat ID based on two user IDs
//    fun getChatId(user1: String, user2: String): String {
//        return if (user1 < user2) user1 + user2 else user2 + user1
//    }
//
//    // Start observing messages for this chat
//    fun startObserving(user1: String, user2: String) {
//        chatId = getChatId(user1, user2)
//
//        db.collection("chats")
//            .document(chatId!!)
//            .collection("messages")
//            .orderBy("timeStamp", Query.Direction.ASCENDING)
//            .addSnapshotListener { snapshot, _ ->
//                if (snapshot != null) {
//                    val list = snapshot.documents.mapNotNull { doc ->
//                        doc.toObject(Message::class.java)?.copy(receivedId = doc.id)
//                    }
//                    _messages.value = list
//                }
//            }
//    }
//
//    // Send message to this chat
//    fun sendMessage(senderId: String, text: String) {
//        val id = chatId ?: return // don’t send if chatId is not set
//
//        viewModelScope.launch {
//            if (text.isBlank()) return@launch
//
//            val message = Message(
//                senderId = senderId,
//                message = text,
//                timeStamp = System.currentTimeMillis()
//            )
//
//            db.collection("chats")
//                .document(id)
//                .collection("messages")
//                .add(message)
//        }
//    }
//}
