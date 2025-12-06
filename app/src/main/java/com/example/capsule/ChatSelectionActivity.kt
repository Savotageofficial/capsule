package com.example.capsule

import ChatHistoryViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.activities.ChatActivity
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.example.capsule.data.model.UserProfile
import com.example.capsule.ui.theme.CapsuleTheme
import com.example.capsule.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class ChatSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CapsuleTheme {
                ChatSelection()

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSelection(
    modifier: Modifier = Modifier,
    viewModel: ChatHistoryViewModel = viewModel()
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var userType by remember { mutableStateOf<String?>(null) }

    val doctors by viewModel.doctors.collectAsState()
    val patients by viewModel.patient.collectAsState()
    val uid: String = auth.currentUser!!.uid

// fetch once
    LaunchedEffect(uid) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                userType = doc.getString("userType")?.lowercase()  // lowercase to normalize
                if (userType == "patient") viewModel.loadPatientChatHistory()
                else if (userType == "doctor") viewModel.loadDoctorChatHistory(uid)
            }
            .addOnFailureListener { e ->
                Log.e("ChatDebug", "Failed to fetch userType", e)
            }
    }

    Column {
        TopAppBar(
            modifier = modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xff5782b3),
                titleContentColor = White,
            ),
            title = {
                Image(
                    painter = painterResource(id = R.drawable.capsuletext),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }
        )

        // 🔹 Only show the list when both userType and data are ready
        when {
            userType == null -> CircularProgressIndicator(modifier = Modifier.padding(50.dp))
            userType == "doctor" && patients.isEmpty() -> CircularProgressIndicator(
                modifier = Modifier.padding(
                    50.dp
                )
            )

            userType == "patient" && doctors.isEmpty() -> CircularProgressIndicator(
                modifier = Modifier.padding(
                    50.dp
                )
            )

            userType == "doctor" -> LazyColumn(
                modifier = modifier.fillMaxSize().padding(top = 50.dp)
            ) {
                items(patients) { patient ->
                    PatientResultCard(patient) {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("Name", patient.name)
                        intent.putExtra("Id", patient.id)
                        context.startActivity(intent)
                    }
                }
            }

            userType == "patient" -> LazyColumn(
                modifier = modifier.fillMaxSize().padding(top = 50.dp)
            ) {
                items(doctors) { doctor ->
                    DoctorResultCard(doctor) {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("Name", doctor.name)
                        intent.putExtra("Id", doctor.id)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}



    @Composable
fun ChatItem(image: Int, title : String, modifier: Modifier = Modifier) {


    Row(
        modifier = modifier.fillMaxWidth()
            .height(70.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Doctor Image",
            modifier = Modifier
                .clip(CircleShape)
                .weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.weight(4f)
                .fillMaxSize()
        ) {
            Text(
                text = title,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,

                )
        }
//        Column(
//
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//
//        ) {
//
//
//        }

    }

}
@Composable
fun DoctorResultCard(
    doctor: Doctor,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Circular doctor image
            Image(
                painter = painterResource(R.drawable.doc_prof_unloaded),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // -------- NAME --------
                Text(
                    text = "Dr. ${doctor.name}",
                    style = MaterialTheme.typography.titleLarge,   // Larger text
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(2.dp))

                // -------- SPECIALTY --------
                Text(
                    text = doctor.specialty,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                // -------- ADDRESS --------
                Text(
                    text = doctor.clinicAddress,
                    style = MaterialTheme.typography.bodyMedium,   // Larger
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )


                // -------- RATING & PRICE -------
            }
        }
    }
}

@Composable
fun PatientResultCard(
    patient: Patient,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Circular doctor image
            Image(
                painter = painterResource(R.drawable.patient_profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // -------- NAME --------
                Text(
                    text = "Dr. ${patient.name}",
                    style = MaterialTheme.typography.titleLarge,   // Larger text
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(2.dp))

                // -------- RATING & PRICE -------
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatSelectionPreview() {
    CapsuleTheme {
//        ChatItem(R.drawable.doc_prof_unloaded , "mohamed")
//        ChatSelection()
//        DoctorResultCard() { }
    }
}