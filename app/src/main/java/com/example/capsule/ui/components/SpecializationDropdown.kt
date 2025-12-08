package com.example.capsule.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecializationDropdown(
    selectedSpecialty: String,
    onSpecialtySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Specialization",
    placeholder: String = "Select Specialty",
    isRequired: Boolean = false
) {
    val specializations = listOf(
        "Neurologist", "Allergist", "Anesthesiologist", "Cardiologist",
        "Dermatologist", "Endocrinologist", "Emergency Medicine", "Family Physician",
        "Gastroenterologist", "Geriatric Medicine", "Hematologist", "Nephrologist",
        "Oncologist", "Ophthalmologist", "Psychiatrist", "Cardiology", "Pediatrics",
        "Radiology", "Orthopedics", "General Surgery", "Family Medicine", "Gynecology",
        "Endocrinology", "Urology", "General Practitioner"
    )

    var expanded by remember { mutableStateOf(false) }
    var displayText by remember { mutableStateOf(selectedSpecialty) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = displayText.ifBlank { placeholder },
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    if (isRequired) "$label *" else label,
                    color = Color(0xFF6098AA),
                    fontSize = 16.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFDAE9EE),
                unfocusedContainerColor = Color(0xFFDAE9EE),
                cursorColor = Color(0xFF19CEFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = if (displayText.isBlank()) Color(0xFF6098AA) else Color.Black
            ),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            specializations.forEach { specialty ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = specialty,
                            color = if (specialty == selectedSpecialty) Color(0xFF0CA7BA) else Color(0xFF424242),
                            fontSize = 15.sp
                        )
                    },
                    onClick = {
                        displayText = specialty
                        onSpecialtySelected(specialty)
                        expanded = false
                    }
                )
            }
        }
    }
}
