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
import com.example.capsule.ui.components.InfoRow
import com.example.capsule.R
import com.example.capsule.ui.screens.PatientProfileViewModel
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.Green
import com.example.capsule.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPatientProfileScreen(

    onBackClick: () -> Unit = {},
    viewModel: PatientProfileViewModel = PatientProfileViewModel(),
) {
    // Observe the patient from ViewModel
    val patient = viewModel.patient.value

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
                        onClick = { /* TODO: Handle booking */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Text(
                            text = "Make Prescription",
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
                            text = "Start Chat",
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
                Image(
                    painter = painterResource(id = R.drawable.patient_profile),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column() {

                    Text(
                        text = patient.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow(label = stringResource(R.string.date_of_birth), value = patient.dob)
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