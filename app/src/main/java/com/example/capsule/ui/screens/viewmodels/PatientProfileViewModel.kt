package com.example.capsule.ui.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.capsule.model.Patient

class PatientProfileViewModel : ViewModel() {

    // Default state with a sample patient
    private val _patient = mutableStateOf(
        Patient(
            name = "Mohamed Safwat",
            dob = "1/5/2005",
            gender = "Male",
            contact = "0100200300",
            email = "m@gmail.com"
        )
    )
    val patient: State<Patient> = _patient

}