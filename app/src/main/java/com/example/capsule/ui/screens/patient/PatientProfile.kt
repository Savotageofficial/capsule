package com.example.capsule.ui.screens.patient

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.ui.components.InfoCard
import com.example.capsule.ui.components.InfoRow
import com.example.capsule.R
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Gray
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.ProfileImage
import com.example.capsule.util.formatDateOfBirth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    patientId: String? = null,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    // Create ViewModel
    val viewModel = viewModel<PatientViewModel>()

    // Load data from Firebase on first launch
    LaunchedEffect(patientId) {          // The composable first enters the composition
        viewModel.loadCurrentPatientProfile()       // Load current user
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
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage(
                base64Image = patient.profileImageBase64,
                defaultImageRes = R.drawable.doc_prof_unloaded,
                modifier = Modifier.size(120.dp),
                onImageClick = null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = patient.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(28.dp))

            InfoCard(title = stringResource(R.string.personal_information)) {
                InfoRow("Full Name", patient.name)
                InfoRow("Date of Birth", formatDateOfBirth(patient.dob))
                InfoRow("Gender", patient.gender)
                InfoRow("Contact", patient.contact)
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
}


@Preview(showBackground = true)
@Composable
fun PatientProfileScreenPreview() {
    MaterialTheme {
        PatientProfileScreen("123")
    }
}
