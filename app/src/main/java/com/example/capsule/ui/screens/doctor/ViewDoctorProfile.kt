package com.example.capsule.ui.screens.doctor

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.activities.ChatActivity
import com.example.capsule.R
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.ui.screens.booking.BookingBottomSheet
import com.example.capsule.ui.components.InfoCard
import com.example.capsule.ui.screens.patient.PatientViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Gold
import com.example.capsule.ui.theme.Green
import com.example.capsule.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import com.example.capsule.ui.theme.WhiteSmoke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDoctorProfileScreen(
    doctorId: String? = null,
    onBackClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onBookingSuccess: (Long, TimeSlot, String) -> Unit = { _, _, _ -> },
    doctorViewModel: DoctorViewModel = viewModel(),
    patientViewModel: PatientViewModel = viewModel()
) {
    val doctor = doctorViewModel.doctor.value
    val patient = patientViewModel.patient.value
    var showBookingSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Load doctor and patient data
    LaunchedEffect(doctorId) {
        if (doctorId == null) {
            doctorViewModel.loadCurrentDoctorProfile()
        } else {
            doctorViewModel.loadDoctorProfileById(doctorId)
        }
        patientViewModel.loadCurrentPatientProfile()
    }

    // Show loading state
    if (doctor == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profile_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF0A3140)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color(0xFF0CA7BA),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { onBackClick() }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Color(0xFF0A3140)
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = WhiteSmoke,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (patient == null) {
                                Toast.makeText(
                                    context,
                                    "Loading patient data...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                showBookingSheet = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Green),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        enabled = patient != null
                    ) {
                        Text(
                            text = stringResource(R.string.book_appointment),
                            fontSize = 18.sp
                        )
                    }

                    Button(
                        onClick = {
                            val docref = db.collection("patients").document(currentUser?.uid!!)

                            docref.get().addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    // 1. FIX THE CRASH: Use .get() and cast it safely
                                    // We expect an ArrayList, so we cast it to MutableList<String>
                                    val fetchedHistory = document.get("msgHistory") as? MutableList<String>

                                    // If the field doesn't exist yet, create a new list
                                    val msgHistory = fetchedHistory ?: mutableListOf()

                                    // 2. FIX THE LOGIC: Do the check and update INSIDE this block
                                    if (doctorId != null && !msgHistory.contains(doctorId)) {
                                        msgHistory.add(doctorId)

                                        // Perform the update strictly after we know the data is modified
                                        docref.update("msgHistory", msgHistory)
                                            .addOnSuccessListener {
                                                // Success message here
                                            }
                                            .addOnFailureListener { e ->
                                                // Handle update failure
                                            }
                                    }
                                }
                            }.addOnFailureListener { exception ->
                                // Handle get() failure
                            }



                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra("Name", doctor.name)
                            intent.putExtra("Id", doctor.id)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Blue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = stringResource(R.string.start_chat),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(8.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Image(
                painter = painterResource(
                    id = doctor.profileImageRes ?: R.drawable.doc_prof_unloaded
                ),
                contentDescription = "Doctor Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Name and Specialty
            Text(doctor.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = doctor.specialty, fontSize = 16.sp, color = Blue)

            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "Rating Star",
                    tint = Gold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${doctor.rating}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${doctor.reviewsCount} reviews)",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Section
            InfoCard(title = stringResource(R.string.about)) {
                Text(text = doctor.bio, fontSize = 15.sp)
            }

            // License & Specialization
            InfoCard(title = stringResource(R.string.license_specialization)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_specialization),
                        contentDescription = "Specialization Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(doctor.specialty)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_experience),
                        contentDescription = "Experience Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(doctor.experience)
                }
            }

            // Location
            InfoCard(title = stringResource(R.string.location)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(doctor.clinicName, fontWeight = FontWeight.Medium)
                        Text(doctor.clinicAddress, color = Color.Gray)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if (doctor.locationUrl.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "No location available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val intent = Intent(Intent.ACTION_VIEW, doctor.locationUrl.toUri())
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Blue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.view_on_map),
                            color = Blue,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Add this section after the Location InfoCard and before Availability Section

            InfoCard(title = "Session Price") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Consultation Fee",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                    Text(
                        text = doctor.formattedSessionPrice,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Blue
                    )
                }
            }

            // Availability Section
            InfoCard(title = stringResource(R.string.availability)) {
                if (doctor.availability.isEmpty()) {
                    Text("Availability not set", color = Color.Gray, fontSize = 15.sp)
                } else {
                    Column {
                        doctor.availability.forEach { (day, slots) ->
                            if (slots.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = day,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = slots.joinToString(", ") {
                                            "${it.start} - ${it.end}"
                                        },
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (day != doctor.availability.keys.lastOrNull()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Booking Bottom Sheet
    if (showBookingSheet && patient != null) {
        BookingBottomSheet(
            doctor = doctor,
            patient = patient,
            onBookingConfirmed = { timestamp, slot, type ->
                showBookingSheet = false
                onBookingSuccess(timestamp, slot, type)
                Toast.makeText(
                    context,
                    "Appointment booked successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { showBookingSheet = false }
        )
    }
}