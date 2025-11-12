package com.example.capsule.ui.screens.profiles

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.ui.components.InfoCard
import com.example.capsule.R
import com.example.capsule.ui.screens.viewmodels.DoctorProfileViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Gold
import com.example.capsule.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileScreen(
    onEditClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: DoctorProfileViewModel = DoctorProfileViewModel()
) {

    val doctor = viewModel.doctor.value

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
                    IconButton(onClick = onEditClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit"
                        )
                    }
                }
            )
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(doctor.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(doctor.specialty, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
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

            InfoCard(title = stringResource(R.string.availability)) {
                Text(doctor.availability, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { /* Open settings */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(stringResource(R.string.settings))
            }
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { /* Logout */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(stringResource(R.string.logout))
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorProfileScreenPreview() {
    MaterialTheme {
        DoctorProfileScreen()
    }
}
