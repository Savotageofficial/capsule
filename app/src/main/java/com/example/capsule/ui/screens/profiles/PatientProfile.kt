package com.example.capsule.ui.screens.profiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.components.InfoCard
import com.example.capsule.ui.components.InfoRow
import com.example.capsule.R
import com.example.capsule.ui.screens.viewmodels.PatientProfileViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Gray
import com.example.capsule.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    patientId: String? = null,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    // Create ViewModel
    val viewModel = viewModel<PatientProfileViewModel>()

    // Load data from Firebase on first launch
    LaunchedEffect(patientId) {
        if (patientId == null) {
            viewModel.loadCurrentPatientProfile() // Load current user
        } else {
            viewModel.loadPatientProfileById(patientId) // Load specific patient
        }
    }

    // Observe patient State
    val patient = viewModel.patient.value

    // If still loading
    if (patient == null) {
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
                        text = stringResource(R.string.profile_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            patient.profileImageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = patient.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(28.dp))

            InfoCard(title = stringResource(R.string.personal_information)) {
                InfoRow("Full Name", patient.name)
                InfoRow("Date of Birth", patient.dob)
                InfoRow("Gender", patient.gender)
                InfoRow("Contact", patient.contact)
                InfoRow("Email", patient.email)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.your_data_is_secure_and_private),
                    fontSize = 14.sp,
                    color = Gray
                )

                Button(
                    onClick = onSettingsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.settings))
                }

                Button(
                    onClick = { /* Logout */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.logout))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PatientProfileScreenPreview() {
    MaterialTheme {
        PatientProfileScreen("123")
    }
}