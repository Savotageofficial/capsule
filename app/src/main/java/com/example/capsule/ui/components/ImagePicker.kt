package com.example.capsule.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capsule.R
import com.example.capsule.util.ImageUtils

@Composable
fun ImagePicker(
    currentImageUrl: String? = null,
    onImagePicked: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }

    // Use ActivityResultContracts for both gallery and camera
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isLoading = true
            val file = ImageUtils.createFileFromUri(context, uri)
            val base64 = file?.path?.let { ImageUtils.compressImageAndConvertToBase64(it) }
            isLoading = false
            onImagePicked(base64)
        }
    }

    Box(
        modifier = modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        // Profile image container
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showOptions = true }
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Display current image or default
                val bitmap = remember(currentImageUrl) {
                    currentImageUrl?.let { ImageUtils.base64ToBitmap(it) }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.doc_prof_unloaded),
                        contentDescription = "Default Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Camera icon overlay
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.BottomEnd)
                .clickable { showOptions = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Change Photo",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    // Options dialog
    if (showOptions) {
        AlertDialog(
            onDismissRequest = { showOptions = false },
            title = { Text("Change Profile Photo") },
            text = {
                Column {
                    Text(
                        text = "Choose an option",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(
                        onClick = {
                            showOptions = false
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Choose from Gallery")
                    }

                    // For Camera option, you would need additional permissions
                    // For now, let's just show gallery option
                    /*
                    TextButton(
                        onClick = {
                            showOptions = false
                            // Implement camera launch here
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Take Photo")
                    }
                    */

                    // Remove photo option if there's a current image
                    if (currentImageUrl != null) {
                        TextButton(
                            onClick = {
                                showOptions = false
                                onImagePicked(null) // Set to null to remove
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Remove Photo", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showOptions = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}