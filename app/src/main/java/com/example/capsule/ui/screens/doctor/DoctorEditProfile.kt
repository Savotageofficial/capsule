package com.example.capsule.ui.screens.doctor

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.ui.components.AvailabilityBottomSheet
import com.example.capsule.ui.components.ImagePicker
import com.example.capsule.ui.components.SpecializationDropdown
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.WhiteSmoke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorEditProfileScreen(
    viewModel: DoctorViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val doctor = viewModel.doctor.value
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadCurrentDoctorProfile()
    }

    if (doctor == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Editable fields
    var name by remember { mutableStateOf(TextFieldValue(doctor.name)) }
    var bio by remember { mutableStateOf(TextFieldValue(doctor.bio)) }
    var experience by remember { mutableStateOf(TextFieldValue(doctor.experience)) }
    var clinicName by remember { mutableStateOf(TextFieldValue(doctor.clinicName)) }
    var clinicAddress by remember { mutableStateOf(TextFieldValue(doctor.clinicAddress)) }
    var locationUrl by remember { mutableStateOf(TextFieldValue(doctor.locationUrl)) }
    var sessionPrice by remember { mutableStateOf(TextFieldValue(doctor.sessionPrice.toString())) }
    var specialty by remember { mutableStateOf(doctor.specialty) }

    // Profile image state
    var selectedImageBase64 by remember { mutableStateOf<String?>(doctor.profileImageBase64) }
    var showAvailabilitySheet by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(
                    stringResource(R.string.edit_profile),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color(0xFF0CA7BA),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { onBackClick() }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Color(0xFF0A3140)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(10.dp))

            // Profile Image Picker
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ImagePicker(
                    currentImageUrl = selectedImageBase64,
                    onImagePicked = { newBase64Image ->
                        selectedImageBase64 = newBase64Image
                    }
                )
            }
            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.full_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    SpecializationDropdown(
                        selectedSpecialty = specialty,
                        onSpecialtySelected = { specialty = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = "Specialization",
                        placeholder = "Select specialty"
                    )

                    Spacer(Modifier.height(12.dp))

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

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text(stringResource(R.string.experience)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = clinicName,
                        onValueChange = { clinicName = it },
                        label = { Text(stringResource(R.string.clinic_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = clinicAddress,
                        onValueChange = { clinicAddress = it },
                        label = { Text(stringResource(R.string.clinic_address)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = locationUrl,
                        onValueChange = { locationUrl = it },
                        label = { Text(stringResource(R.string.clinic_address_link_on_maps)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = sessionPrice,
                        onValueChange = {
                            // Allow only numbers and decimal point
                            if (it.text.matches(Regex("^\\d*\\.?\\d*$"))) {
                                sessionPrice = it
                            }
                        },
                        label = { Text(stringResource(R.string.session_price_egp)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        prefix = { Text(stringResource(R.string.egp)) }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Availability Section
                    Text(
                        text = stringResource(R.string.availability),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedButton(
                        onClick = { showAvailabilitySheet = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(doctor.availabilityDisplay) // Use the display property
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Save button
            // In DoctorEditProfile.kt, replace the save button logic:

            Button(
                onClick = {
                    isLoading = true

                    val profileData = mapOf(
                        "name" to name.text,
                        "specialty" to specialty,
                        "bio" to bio.text,
                        "experience" to experience.text,
                        "clinicName" to clinicName.text,
                        "clinicAddress" to clinicAddress.text,
                        "locationUrl" to locationUrl.text,
                        "sessionPrice" to sessionPrice.text.toDouble()
                    )

                    // Determine image action
                    val imageAction = when {
                        selectedImageBase64 == null && doctor.profileImageBase64 != null ->
                            "delete" // Remove existing image
                        selectedImageBase64 != null && selectedImageBase64 != doctor.profileImageBase64 ->
                            "upload" // Upload new/changed image
                        else -> "none" // No change
                    }

                    // Helper function to handle profile update
                    fun handleProfileUpdate(success: Boolean, error: String? = null) {
                        if (success) {
                            viewModel.updateDoctorProfile(profileData) { updateSuccess ->
                                isLoading = false
                                if (updateSuccess) {
                                    showSaveDialog = true
                                } else {
                                    showErrorDialog = true
                                }
                            }
                        } else {
                            isLoading = false
                            showErrorDialog = true
                        }
                    }

                    // Execute image action first (if needed), then update profile
                    when (imageAction) {
                        "delete" -> {
                            viewModel.deleteProfileImage { success, error ->
                                handleProfileUpdate(success, error)
                            }
                        }
                        "upload" -> {
                            selectedImageBase64?.let { base64 ->
                                viewModel.uploadProfileImage(base64) { success, error ->
                                    handleProfileUpdate(success, error)
                                }
                            } ?: run {
                                handleProfileUpdate(true) // No image to upload
                            }
                        }
                        "none" -> {
                            viewModel.updateDoctorProfile(profileData) { success ->
                                isLoading = false
                                if (success) {
                                    showSaveDialog = true
                                } else {
                                    showErrorDialog = true
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(Cyan),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.save_changes), fontSize = 17.sp)
                }
            }
        }
    }

    // ---- Dialogs and Bottom Sheets ----
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) { Text(stringResource(R.string.ok)) }
            },
            title = { Text(stringResource(R.string.update_failed)) },
            text = { Text(stringResource(R.string.failed_to_update_your_profile_please_try_again)) }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showSaveDialog = false
                    onBackClick()
                }) { Text(stringResource(R.string.ok)) }
            },
            title = { Text(stringResource(R.string.profile_updated)) },
            text = { Text(stringResource(R.string.your_profile_has_been_successfully_updated)) }
        )
    }

    // Availability Bottom Sheet
    AvailabilityBottomSheet(
        show = showAvailabilitySheet,
        viewModel = viewModel,
        onDismiss = { showAvailabilitySheet = false }
    )
}
