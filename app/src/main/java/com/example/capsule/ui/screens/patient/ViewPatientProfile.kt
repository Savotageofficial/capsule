package com.example.capsule.ui.screens.patient

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.activities.ChatActivity
import com.example.capsule.ui.components.InfoRow
import com.example.capsule.R
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Green
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.White
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.ProfileImage
import com.example.capsule.util.formatDateOfBirth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPatientProfileScreen(
    patientId: String? = null,
    onBackClick: () -> Unit = {},
    viewModel: PatientViewModel = viewModel(),
    onMakePrescriptionClick: (patientId: String, patientName: String) -> Unit = { _, _ -> },
) {
    val patient = viewModel.patient.value
    val context = LocalContext.current

    // Load patient data when screen opens
    LaunchedEffect(patientId) {
        if (patientId == null) {
            viewModel.loadCurrentPatientProfile()
        } else {
            viewModel.loadPatientProfileById(patientId)
        }
    }

    // Show loading state
    if (patient == null) {
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Teal
                )
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
                        onClick = {
                                onMakePrescriptionClick(patient.id, patient.name)

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.make_a_prescription),
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra("Name", patient.name)
                            intent.putExtra("Id", patient.id)
                            context.startActivity(intent)
                        },
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
                .background(WhiteSmoke)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .padding(vertical = 20.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ProfileImage(
                    base64Image = patient.profileImageBase64,
                    defaultImageRes = R.drawable.patient_profile,
                    modifier = Modifier.size(120.dp),
                    onImageClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = patient.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow(
                        label = stringResource(R.string.date_of_birth),
                        value = formatDateOfBirth(patient.dob)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    InfoRow(label = stringResource(R.string.gender), value = patient.gender)
                    Spacer(modifier = Modifier.height(4.dp))
                    InfoRow(label = stringResource(R.string.contact), value = patient.contact)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewPatientProfileScreenPreview() {
    MaterialTheme {
        ViewPatientProfileScreen()
    }
}
