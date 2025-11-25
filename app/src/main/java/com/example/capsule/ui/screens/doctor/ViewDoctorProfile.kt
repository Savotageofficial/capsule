package com.example.capsule.ui.screens.doctor

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.components.InfoCard
import com.example.capsule.R
import com.example.capsule.ui.components.BookingBottomSheet
import com.example.capsule.ui.screens.patient.PatientProfileViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Gold
import com.example.capsule.ui.theme.Green
import com.example.capsule.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDoctorProfileScreen(
    doctorId: String? = null,
    onBackClick: () -> Unit = {},
    viewModel: DoctorProfileViewModel = viewModel(),
    patientViewModel: PatientProfileViewModel = viewModel()
) {
    val doctor = viewModel.doctor.value
    var showBookingSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load doctor data when screen opens
    LaunchedEffect(doctorId) {
        if (doctorId == null) {
            viewModel.loadCurrentDoctorProfile()
        } else {
            viewModel.loadDoctorProfileById(doctorId)
        }
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
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Save Doctor */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bookmark),
                            contentDescription = "Bookmark"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = White,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showBookingSheet = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.book_appointment),
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { /* TODO: Handle chat */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.start_chat),
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    ) { padding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(8.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            doctor.profileImageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Doctor Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                // Fallback image if profileImageRes is null
                Image(
                    painter = painterResource(id = R.drawable.doc_prof_unloaded),
                    contentDescription = "Doctor Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(doctor.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                text = doctor.specialty,
                fontSize = 16.sp,
                color = Blue
            )
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

            InfoCard(title = stringResource(R.string.about)) {
                Text(text = doctor.bio, fontSize = 15.sp)
            }

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
                                // Show message if no location URL
                                Toast.makeText(context, "No location available", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val intent = Intent(Intent.ACTION_VIEW, doctor.locationUrl.toUri())
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Blue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.view_on_map),
                            color = Blue,
                            fontSize = 14.sp
                        )
                    }
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
                                        text = slots.joinToString(", ") { "${it.start} - ${it.end}" },
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

            if (showBookingSheet) {
                BookingBottomSheet(
                    doctor = doctor,
                    onConfirm = { timestamp, slot, type ->
                        patientViewModel.bookAppointment(doctor, timestamp, slot, type) { success ->
                            showBookingSheet = false
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Appointment booked successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackClick() // Go back to previous screen
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to book appointment",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onDismiss = { showBookingSheet = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewDoctorProfileScreenPreview() {
    MaterialTheme {
        ViewDoctorProfileScreen()
    }
}