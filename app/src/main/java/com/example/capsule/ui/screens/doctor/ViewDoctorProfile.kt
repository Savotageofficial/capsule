package com.example.capsule.ui.screens.doctor

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.activities.ChatActivity
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.ui.components.InfoCard
import com.example.capsule.ui.screens.booking.BookingBottomSheet
import com.example.capsule.ui.screens.patient.PatientViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Gold
import com.example.capsule.ui.theme.Green
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.ProfileImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDoctorProfileScreen(
    doctorId: String? = null,
    onBackClick: () -> Unit = {},
    onBookingSuccess: (Long, TimeSlot, String) -> Unit = { _, _, _ -> },
    doctorViewModel: DoctorViewModel = viewModel(),
    patientViewModel: PatientViewModel = viewModel()
) {
    val doctor = doctorViewModel.doctor.value
    val patient = patientViewModel.patient.value
    var showBookingSheet by remember { mutableStateOf(false) }
    var showRatingSheet by remember { mutableStateOf(false) }
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
                                    val fetchedHistory =
                                        document.get("msgHistory") as? MutableList<String>
                                    val msgHistory = fetchedHistory ?: mutableListOf()

                                    if (doctorId != null && !msgHistory.contains(doctorId)) {
                                        msgHistory.add(doctorId)
                                        docref.update("msgHistory", msgHistory)
                                    }
                                }
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
            ProfileImage(
                base64Image = doctor.profileImageBase64,
                defaultImageRes = R.drawable.doc_prof_unloaded,
                modifier = Modifier.size(120.dp),
                onImageClick = null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Name and Specialty
            Text(doctor.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = doctor.specialty, fontSize = 16.sp, color = Blue)

            // Rating Display Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        // Show rating bottom sheet when clicked
                        showRatingSheet = true
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "Rating Star",
                    tint = Gold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", doctor.rating),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${doctor.reviewsCount} reviews)",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Rate",
                    tint = Blue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Rate",
                    color = Blue,
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

            // Session Price
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

    // Rating Bottom Sheet
    if (showRatingSheet) {
        RatingBottomSheet(
            doctor = doctor,
            onRatingSubmitted = { success ->
                showRatingSheet = false
                if (success) {
                    Toast.makeText(
                        context,
                        "Thank you for your rating!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onDismiss = { showRatingSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingBottomSheet(
    doctor: Doctor,
    onRatingSubmitted: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val context = LocalContext.current
    val doctorViewModel: DoctorViewModel = viewModel()
    var selectedRating by remember { mutableIntStateOf(doctorViewModel.currentUserRating.intValue) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Check if user has already rated this doctor
    LaunchedEffect(doctor.id, currentUser?.uid) {
        if (doctor.id.isNotEmpty() && currentUser?.uid != null) {
            doctorViewModel.checkIfUserHasRated(doctor.id, currentUser.uid)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Rate Dr. ${doctor.name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtitle
            Text(
                text = "How was your experience with this doctor?",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Rating Stars
            if (doctorViewModel.isRatingLoading.value) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else if (doctorViewModel.hasRated.value) {
                // Already rated state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Already rated",
                        tint = Green,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "You've already rated this doctor",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Your rating: ${doctorViewModel.currentUserRating.value} stars",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Blue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            } else {
                // Rating interface
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Stars
                    RatingBar(
                        currentRating = selectedRating,
                        onRatingSelected = { rating ->
                            selectedRating = rating
                        },
                        starSize = 40.dp
                    )

                    // Rating labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Poor", fontSize = 12.sp, color = Color.Gray)
                        Text("Excellent", fontSize = 12.sp, color = Color.Gray)
                    }

                    // Current doctor rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "Current rating",
                            tint = Gold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Current rating: ${String.format("%.1f", doctor.rating)}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${doctor.reviewsCount} reviews)",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // Submit button
                    Button(
                        onClick = {
                            if (selectedRating > 0 && currentUser?.uid != null) {
                                isSubmitting = true
                                doctorViewModel.submitRating(
                                    doctorId = doctor.id,
                                    patientId = currentUser.uid,
                                    rating = selectedRating
                                ) { success ->
                                    isSubmitting = false
                                    onRatingSubmitted(success)
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please select a rating first",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Blue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = selectedRating > 0 && !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Submit Rating",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Cancel button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RatingBar(
    currentRating: Int,
    onRatingSelected: (Int) -> Unit,
    enabled: Boolean = true,
    starSize: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val filled = i <= currentRating
            Icon(
                painter = painterResource(
                    id = if (filled) R.drawable.ic_star_filled else R.drawable.ic_star
                ),
                contentDescription = "$i stars",
                tint = if (enabled && filled) Gold else Color.LightGray,
                modifier = Modifier
                    .size(starSize)
                    .clickable(enabled = enabled) { onRatingSelected(i) }
                    .padding(4.dp)
            )
        }
    }
}