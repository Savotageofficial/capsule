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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.ui.components.InfoCard
import com.example.capsule.R
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Gold
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.ProfileImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileScreen(
    doctorId: String? = null,
    onEditClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    // Create ViewModel
    val viewModel = viewModel<DoctorViewModel>()

    // Load data from Firebase on first launch
    LaunchedEffect(doctorId) {
        if (doctorId == null) {
            viewModel.loadCurrentDoctorProfile() // Load current user
        } else {
            viewModel.loadDoctorProfileById(doctorId) // Load specific doctor
        }
    }

    // Observe doctor State
    val doctor = viewModel.doctor.value
    val context = LocalContext.current

    // If still loading
    if (doctor == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Cyan,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { onBackClick() }
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit",
                            tint = Cyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Teal
                )
            )
        }
    ) { padding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            ProfileImage(
                base64Image = doctor.profileImageBase64,
                defaultImageRes = R.drawable.doc_prof_unloaded,
                modifier = Modifier.size(120.dp),
                onImageClick = {
                    // You can add image picker functionality here
                    // or navigate to edit screen
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(doctor.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(doctor.specialty, fontSize = 18.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(22.dp))

            InfoCard(title = "Bio") {
                Text(doctor.bio, fontSize = 15.sp)
            }

            InfoCard(title = stringResource(R.string.rating)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Based on ${doctor.reviewsCount} patient reviews",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "Rating Star",
                            tint = Gold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${doctor.rating}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
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
                                Toast.makeText(context, "No Location Provided", Toast.LENGTH_SHORT)
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
                            text = "View on Map",
                            color = Blue,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Add this section after the Location InfoCard and before Availability Section

            InfoCard(title = stringResource(R.string.session_price)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.consultation_fee),
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

            InfoCard(title = stringResource(R.string.availability)) {
                if (doctor.availability.isEmpty()) {
                    Text("Not set", color = Color.Gray, fontSize = 15.sp)
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
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = slots.joinToString(", ") { "${it.start} - ${it.end}" },
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onSettingsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Cyan
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Text(stringResource(R.string.settings))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorProfileScreenPreview() {
    MaterialTheme {
        DoctorProfileScreen("5412")
    }
}