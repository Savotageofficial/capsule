
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
        val doctorList = ids.mapNotNull { id ->
            try {
                val docSnapshot = db.collection("doctors")
                    .document(id)
                    .get()
                    .await()

                Doctor(
                    id = docSnapshot.id,
                    name = docSnapshot.getString("name") ?: "Unknown",
                    specialty = docSnapshot.getString("specialty") ?: "",
                    profileImageBase64 = docSnapshot.getString("profileImageBase64")
                )
            } catch (e: Exception) {
                Log.e("ChatDebug", "Error fetching doctor $id", e)
                null
            }
        }

        _doctors.value = doctorList
    }

    private suspend fun fetchPatients(ids: List<String>) {
        val patientList = ids.mapNotNull { id ->
            try {
                val docSnapshot = db.collection("patients")
                    .document(id)
                    .get()
                    .await()

                Patient(
                    id = docSnapshot.id,
                    name = docSnapshot.getString("name") ?: "Unknown",
                    profileImageBase64 = docSnapshot.getString("profileImageBase64")
                )
            } catch (e: Exception) {
                Log.e("ChatDebug", "Error fetching patient $id", e)
                null
            }
        }

        Log.d("ChatDebug", "Fetched patients: $patientList")
        _patients.value = patientList
    }
//would be faster to fetch all and compare all, but i am too lazy, hey it works!
}

