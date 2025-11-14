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
import com.example.capsule.ui.screens.viewmodels.PatientProfileViewModel
import com.example.capsule.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientEditProfileScreen(
    viewModel: PatientProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val patient = viewModel.patient.value
    val scrollState = rememberScrollState()

    // Load current patient when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadCurrentPatientProfile()
    }

    // If still loading or no patient
    if (patient == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Editable fields with current patient data
    var name by remember { mutableStateOf(TextFieldValue(patient.name)) }
    var dob by remember { mutableStateOf(TextFieldValue(patient.dob)) }
    var gender by remember { mutableStateOf(TextFieldValue(patient.gender)) }
    var contact by remember { mutableStateOf(TextFieldValue(patient.contact)) }
    var email by remember { mutableStateOf(TextFieldValue(patient.email)) }

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
            // Editable fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text(stringResource(R.string.date_of_birth)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text(stringResource(R.string.gender)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text(stringResource(R.string.contact)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    // This saves to Firebase
                    val updatedData = mapOf(
                        "name" to name.text,
                        "dob" to dob.text,
                        "gender" to gender.text,
                        "contact" to contact.text,
                        "email" to email.text       // TODO: will be removed
                    )

                    viewModel.updatePatientProfile(updatedData) { success ->
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

    // Dialog shown when saving is done
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
fun PatientEditProfileScreenPreview() {
    MaterialTheme {
        PatientEditProfileScreen()
    }
}
