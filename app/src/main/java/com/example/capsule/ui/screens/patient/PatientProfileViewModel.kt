package com.example.capsule.ui.screens.patient

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.Patient
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class PatientProfileViewModel : ViewModel() {
    private val repo = ProfileRepository.getInstance()

    private val _patient = mutableStateOf<Patient?>(null)
    val patient = _patient

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    fun loadCurrentPatientProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getCurrentPatient {
                _patient.value = it
                _isLoading.value = false
            }
        }
    }

    fun loadPatientProfileById(patientId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repo.getPatientById(patientId) {
                _patient.value = it
                _isLoading.value = false
            }
        }
    }

    fun updatePatientProfile(data: Map<String, Any>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            repo.updateCurrentPatient(data) { success ->
                if (success) {
                    _patient.value = _patient.value?.copy(
                        name = data["name"] as? String ?: _patient.value!!.name,
                        dob = data["dob"] as? String ?: _patient.value!!.dob,
                        gender = data["gender"] as? String ?: _patient.value!!.gender,
                        contact = data["contact"] as? String ?: _patient.value!!.contact,
                        email = data["email"] as? String ?: _patient.value!!.email
                    )
                }
                onDone(success)
            }
        }
    }
}