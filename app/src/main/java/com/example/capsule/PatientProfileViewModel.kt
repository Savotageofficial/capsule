package com.example.capsule

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.capsule.model.Patient

class PatientProfileViewModel : ViewModel() {

    // Default state with a sample patient
    private val _patient = mutableStateOf(
        Patient(
            name = "Loading...",
            dob = "",
            gender = "",
            contact = "",
            email = "",
            medicalHistory = ""
        )
    )
    val patient: State<Patient> = _patient

}
