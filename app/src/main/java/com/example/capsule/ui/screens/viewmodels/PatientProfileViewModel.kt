package com.example.capsule.ui.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Patient
import com.example.capsule.data.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PatientProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository.getInstance()
) : ViewModel() {

    private val _patient = mutableStateOf<Patient?>(null)
    val patient: State<Patient?> = _patient

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // ADD THIS METHOD - Your screens are calling it!
    fun loadPatientProfile(patientId: String) {
        if (patientId == "current" || patientId.isEmpty()) {
            loadCurrentPatientProfile()
        } else {
            loadPatientProfileById(patientId)
        }
    }

    //   LOAD CURRENT PATIENT PROFILE (using Firebase UID)
    fun loadCurrentPatientProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getCurrentPatient { result ->
                _patient.value = result
                _isLoading.value = false
            }
        }
    }

    //   LOAD PATIENT PROFILE BY ID (for viewing other profiles)
    fun loadPatientProfileById(patientId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getPatientById(patientId) { result ->
                _patient.value = result
                _isLoading.value = false
            }
        }
    }

    //   UPDATE PATIENT PROFILE
    fun updatePatientProfile(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.updateCurrentPatient(data) { success ->
                if (success) {
                    // Update UI instantly
                    val old = _patient.value
                    if (old != null) {
                        val updated = old.copy(
                            name = data["name"] as? String ?: old.name,
                            dob = data["dob"] as? String ?: old.dob,
                            gender = data["gender"] as? String ?: old.gender,
                            contact = data["contact"] as? String ?: old.contact,
                            email = data["email"] as? String ?: old.email
                        )
                        _patient.value = updated
                    }
                }
                onDone(success)
            }
        }
    }

    //   CREATE PATIENT PROFILE (for signup)
    fun createPatientProfile(patient: Patient, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            profileRepository.createPatient(patient) { success ->
                if (success) {
                    _patient.value = patient.copy(id = FirebaseAuth.getInstance().currentUser?.uid ?: "")
                }
                onDone(success)
            }
        }
    }
}