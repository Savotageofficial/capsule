package com.example.capsule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.capsule.data.model.Country
import com.example.capsule.data.model.countries
import com.example.capsule.util.formatPhoneNumber

fun isValidPhone(phone: String): Boolean {
    return phone.filter { it.isDigit() }.length == 11
}

@Composable
fun PhoneNumberField(
    label: String = "Phone Number",
    phone: TextFieldValue,
    onPhoneChange: (TextFieldValue) -> Unit
) {
    var selectedCountry by remember { mutableStateOf(countries.first()) }
    var showCountryPicker by remember { mutableStateOf(false) }

    val rawDigits = phone.text.filter { it.isDigit() }
    val formatted = formatPhoneNumber(rawDigits)

    val valid = isValidPhone(rawDigits)

    Column {
        OutlinedTextField(
            value = phone.copy(text = formatted),
            onValueChange = { newValue ->
                val digitsOnly = newValue.text.filter { it.isDigit() }
                if (digitsOnly.length <= 11) {
                    onPhoneChange(
                        newValue.copy(text = formatPhoneNumber(digitsOnly))
                    )
                }
            },
            label = { Text(label) },
            leadingIcon = {
                Row(
                    modifier = Modifier
                        .clickable { showCountryPicker = true }
                ) {
                    Text(selectedCountry.code)
                }
            },
            isError = !valid && rawDigits.isNotEmpty(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // error message
        if (!valid && rawDigits.isNotEmpty()) {
            Text(
                text = "Invalid phone number",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // country picker popup
        if (showCountryPicker) {
            CountryPickerDialog(
                onSelect = { selectedCountry = it },
                onDismiss = { showCountryPicker = false }
            )
        }
    }
}

@Composable
fun CountryPickerDialog(
    onSelect: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Country") },
        text = {
            Column {
                countries.forEach { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(country)
                                onDismiss()
                            }
                            .padding(12.dp)
                    ) {
                        Text("${country.name} (${country.code})")
                    }
                }
            }
        },
        confirmButton = {}
    )
}