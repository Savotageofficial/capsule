package com.example.capsule.ui.screens.profiles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.ui.screens.viewmodels.DoctorProfileViewModel
import com.example.capsule.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorEditProfileScreen(
    viewModel: DoctorProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val doctor = viewModel.doctor.value
    val scrollState = rememberScrollState()

    // Load current doctor when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadCurrentDoctorProfile()
    }

    // If still loading or no doctor
    if (doctor == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Editable fields
    var name by remember { mutableStateOf(TextFieldValue(doctor.name)) }
    var specialty by remember { mutableStateOf(TextFieldValue(doctor.specialty)) }
    var bio by remember { mutableStateOf(TextFieldValue(doctor.bio)) }
    var experience by remember { mutableStateOf(TextFieldValue(doctor.experience)) }
    var clinicName by remember { mutableStateOf(TextFieldValue(doctor.clinicName)) }
    var clinicAddress by remember { mutableStateOf(TextFieldValue(doctor.clinicAddress)) }
    var locationUrl by remember { mutableStateOf(TextFieldValue(doctor.locationUrl)) }
    var availability by remember { mutableStateOf(TextFieldValue(doctor.availability)) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.edit_profile)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = specialty,
                onValueChange = { specialty = it },
                label = { Text(stringResource(R.string.specialization)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text(stringResource(R.string.about)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                singleLine = false,
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = experience,
                onValueChange = { experience = it },
                label = { Text(stringResource(R.string.experience)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clinicName,
                onValueChange = { clinicName = it },
                label = { Text(stringResource(R.string.clinic_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clinicAddress,
                onValueChange = { clinicAddress = it },
                label = { Text(stringResource(R.string.clinic_address)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = locationUrl,
                onValueChange = { locationUrl = it },
                label = { Text(stringResource(R.string.clinic_address_link_on_maps)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = availability,
                onValueChange = { availability = it },
                label = { Text(stringResource(R.string.availability)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true

                    // map snt to firebase
                    val updatedData = mapOf(
                        "name" to name.text,
                        "specialty" to specialty.text,
                        "bio" to bio.text,
                        "experience" to experience.text,
                        "clinicName" to clinicName.text,
                        "clinicAddress" to clinicAddress.text,
                        "locationUrl" to locationUrl.text,
                        "availability" to availability.text
                    )

                    viewModel.updateDoctorProfile(updatedData) { success ->
                        isLoading = false
                        if (success) {
                            showSaveDialog = true
                            onSaveClick() // Call the callback
                        } else {
                            // TODO: Show error message
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.save_changes), fontSize = 16.sp)
                }
            }
        }
    }

    // Confirmation dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showSaveDialog = false
                    onBackClick()
                }) {
                    Text("OK")
                }
            },
            title = { Text("Profile Updated") },
            text = { Text("Your profile information has been successfully updated.") }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DoctorEditProfileScreenPreview() {
    MaterialTheme {
        DoctorEditProfileScreen()
    }
}
//ignore (by safwat)