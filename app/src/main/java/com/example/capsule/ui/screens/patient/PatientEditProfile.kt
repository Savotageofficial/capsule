package com.example.capsule.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.R
import com.example.capsule.ui.components.DatePickerDialog
import com.example.capsule.ui.components.ImagePicker
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.formatDateOfBirth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientEditProfileScreen(
    viewModel: PatientViewModel = viewModel(),
    onBackClick: () -> Unit = {},
) {
    val patient = viewModel.patient.value
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) { viewModel.loadCurrentPatientProfile() }

    if (patient == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Editable fields
    var name by remember { mutableStateOf(TextFieldValue(patient.name)) }
    var dob by remember { mutableLongStateOf(patient.dob) }
    var gender by remember { mutableStateOf(patient.gender) }
    var contact by remember { mutableStateOf(TextFieldValue(patient.contact)) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Profile image handler (same logic as doctor)
    var selectedImageBase64 by remember { mutableStateOf<String?>(patient.profileImageBase64) }

    // Dropdown
    var genderExpanded by remember { mutableStateOf(false) }
    var genderFieldSize by remember { mutableStateOf(Size.Zero) }
    val genderList = listOf("Male", "Female")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.edit_profile),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Cyan,
                            contentDescription = "Back"
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

        Column(
            modifier = Modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            Spacer(Modifier.height(10.dp))

            // --- Profile Image Picker (same UI as doctor) ---
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ImagePicker(
                    currentImageUrl = selectedImageBase64,
                    onImagePicked = { newImage ->
                        selectedImageBase64 = newImage
                    }
                )
            }

            Spacer(Modifier.height(20.dp))

            // --- Card with form fields ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    // Name
                    FormTextField(
                        label = stringResource(R.string.full_name),
                        value = name,
                        onValueChange = { name = it }
                    )

                    Spacer(Modifier.height(14.dp))

                    // DOB
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_calendar),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (dob > 0) formatDateOfBirth(dob) else "Select Date of Birth")
                    }

                    Spacer(Modifier.height(14.dp))

                    // Gender
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { genderFieldSize = it.size.toSize() },
                        label = { Text(stringResource(R.string.gender)) },
                        trailingIcon = {
                            Icon(
                                if (genderExpanded) Icons.Filled.KeyboardArrowUp
                                else Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    genderExpanded = !genderExpanded
                                }
                            )
                        },
                        singleLine = true
                    )

                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false },
                        modifier = Modifier.width(
                            with(LocalDensity.current) { genderFieldSize.width.toDp() }
                        )
                    ) {
                        genderList.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    gender = label
                                    genderExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Contact
                    FormTextField(
                        label = stringResource(R.string.contact),
                        value = contact,
                        onValueChange = { contact = it },
                        keyboardType = KeyboardType.Phone
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---------------- SAVE BUTTON ----------------
            Button(
                onClick = {
                    isLoading = true

                    val profileData = mapOf(
                        "name" to name.text,
                        "dob" to dob,
                        "gender" to gender,
                        "contact" to contact.text
                    )

                    // Determine image action
                    val imageAction = when {
                        selectedImageBase64 == null && patient.profileImageBase64 != null -> "delete"
                        selectedImageBase64 != null && selectedImageBase64 != patient.profileImageBase64 -> "upload"
                        else -> "none"
                    }

                    fun finishUpdate(success: Boolean) {
                        isLoading = false
                        if (success) showSaveDialog = true
                        else showErrorDialog = true
                    }

                    // Step 1: Handle image action
                    when (imageAction) {
                        "delete" -> {
                            viewModel.deleteProfileImage { success, _ ->
                                if (success)
                                    viewModel.updatePatientProfile(profileData, ::finishUpdate)
                                else finishUpdate(false)
                            }
                        }

                        "upload" -> {
                            selectedImageBase64?.let { img ->
                                viewModel.uploadProfileImage(img) { success, _ ->
                                    if (success)
                                        viewModel.updatePatientProfile(profileData, ::finishUpdate)
                                    else finishUpdate(false)
                                }
                            }
                        }

                        "none" -> {
                            viewModel.updatePatientProfile(profileData, ::finishUpdate)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(Cyan),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
            ) {
                if (isLoading)
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                else
                    Text("Save Changes", fontSize = 17.sp)
            }
        }
    }

    // ------ Dialogs ------
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) { Text("OK") }
            },
            title = { Text("Update Failed") },
            text = { Text("Failed to update your profile. Please try again.") }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                showSaveDialog = false
                onBackClick()
            },
            confirmButton = {
                TextButton(onClick = {
                    showSaveDialog = false
                    onBackClick()
                }) { Text("OK") }
            },
            title = { Text("Profile Updated") },
            text = { Text("Your profile has been successfully updated.") }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = {
                dob = it
                showDatePicker = false
            }
        )
    }
}


@Composable
fun FormTextField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        label = { Text(label) }
    )
}

@Preview(showBackground = true)
@Composable
fun PatientEditProfileScreenPreview() {
    MaterialTheme {
        PatientEditProfileScreen()
    }
}
