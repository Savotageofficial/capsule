package com.example.capsule.ui.screens

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorEditProfileScreen(
    viewModel: DoctorProfileViewModel = DoctorProfileViewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val doctor = viewModel.doctor.value
    val scrollState = rememberScrollState()

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile") },
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
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = specialty,
                onValueChange = { specialty = it },
                label = { Text("Specialization") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
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
                label = { Text("Experience") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clinicName,
                onValueChange = { clinicName = it },
                label = { Text("Clinic Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clinicAddress,
                onValueChange = { clinicAddress = it },
                label = { Text("Clinic Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = locationUrl,
                onValueChange = { locationUrl = it },
                label = { Text("Clinic Address link on maps") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = availability,
                onValueChange = { availability = it },
                label = { Text("Availability") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // TODO: make firebase repo file and and paste this in it
                    /*
                    val db = FirebaseFirestore.getInstance()
                    val userId = FirebaseAuth.getInstance().currentUser?.uid

                    if (userId != null) {
                        val updatedDoctor = mapOf(
                            "name" to name.text,
                            "specialty" to specialty.text,
                            "bio" to bio.text,
                            "experience" to experience.text,
                            "clinicName" to clinicName.text,
                            "clinicAddress" to clinicAddress.text,
                            "availability" to availability.text
                        )

                        db.collection("doctors")
                            .document(userId)
                            .update(updatedDoctor)
                            .addOnSuccessListener {
                                Log.d("DoctorEditProfile", "Doctor profile updated.")
                                showSaveDialog = true
                            }
                            .addOnFailureListener { e ->
                                Log.e("DoctorEditProfile", "Error updating profile", e)
                            }
                    }
                    */

                    onSaveClick()
                    showSaveDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save Changes", fontSize = 16.sp)
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

@Preview(showBackground = true)
@Composable
fun DoctorEditProfileScreenPreview() {
    MaterialTheme {
        DoctorEditProfileScreen()
    }
}
