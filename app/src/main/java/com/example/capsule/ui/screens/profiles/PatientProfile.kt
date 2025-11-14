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

    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
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
            // Profile Image
            patient.profileImageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Doctor Image",
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ID: #${patient.id} ",
                fontSize = 14.sp,
                color = Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            //  Personal Information Card
            InfoCard(title = stringResource(R.string.personal_information)) {
                InfoRow(label = stringResource(R.string.full_name), value = patient.name)
                InfoRow(label = stringResource(R.string.date_of_birth), value = patient.dob)
                InfoRow(label = stringResource(R.string.gender), value = patient.gender)
                InfoRow(label = stringResource(R.string.contact), value = patient.contact)
                InfoRow(label = stringResource(R.string.email), value = patient.email)
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
        PatientProfileScreen()
    }
}

//ignore (by safwat)