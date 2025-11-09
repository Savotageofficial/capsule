package com.example.capsule.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.capsule.model.Patient

class PatientProfileViewModel : ViewModel() {

    // Default state with a sample patient
    private val _patient = mutableStateOf(
        Patient(
            name = "Loading...",
            dob = "",
            gender = "",
            contact = "",
            email = ""
        )
    )
    val patient: State<Patient> = _patient

}