package com.example.capsule.ui.screens.prescription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.*
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrescriptionViewModel : ViewModel() {
    private val repository = ProfileRepository.getInstance()

    // Doctor info
    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    // Selected patient
    private val _selectedPatient = MutableStateFlow<Appointment?>(null)
    val selectedPatient: StateFlow<Appointment?> = _selectedPatient

    // Prescription being created
    private val _prescription = MutableStateFlow(
        Prescription(
            medications = emptyMap()
        )
    )
    val prescription: StateFlow<Prescription> = _prescription

    // Medications list (for UI editing)
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications

    // Appointments list
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCurrentDoctor()
    }

    fun loadCurrentDoctor() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCurrentDoctor { doctor ->
                _doctor.value = doctor
                _isLoading.value = false
            }
        }
    }
    fun loadDoctorAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentDoctor = _doctor.value
            if (currentDoctor != null && currentDoctor.id.isNotBlank()) {
                repository.getDoctorAppointments(currentDoctor.id) { appointments ->
                    _appointments.value = appointments
                    _isLoading.value = false
                }
            } else {
                // If doctor not loaded yet, load doctor then appointments
                repository.getCurrentDoctor { doctor ->
                    _doctor.value = doctor
                    val docId = doctor?.id
                    if (!docId.isNullOrBlank()) {
                        repository.getDoctorAppointments(docId) { appointments ->
                            _appointments.value = appointments
                            _isLoading.value = false
                        }
                    } else {
                        _appointments.value = emptyList()
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun selectPatient(appointment: Appointment) {
        _selectedPatient.value = appointment
        // Initialize prescription with patient info
        _prescription.value = _prescription.value.copy(
            patientId = appointment.patientId,
            patientName = appointment.patientName,
            doctorId = _doctor.value?.id ?: "",
            doctorName = _doctor.value?.name ?: ""
        )
    }

    // Medication management
    fun addMedication() {
        val newList = _medications.value.toMutableList()
        newList.add(Medication())
        _medications.value = newList
        updatePrescriptionFromMedications()
    }

    fun updateMedication(index: Int, medication: Medication) {
        val newList = _medications.value.toMutableList()
        if (index in newList.indices) {
            newList[index] = medication
            _medications.value = newList
            updatePrescriptionFromMedications()
        }
    }

    fun removeMedication(index: Int) {
        val newList = _medications.value.toMutableList()
        if (index in newList.indices) {
            newList.removeAt(index)
            _medications.value = newList
            updatePrescriptionFromMedications()
        }
    }

    private fun updatePrescriptionFromMedications() {
        // Convert list to map keyed by name; skip blank names
        val medsMap = _medications.value
            .filter { it.name.isNotBlank() }
            .associateBy { it.name }
        _prescription.value = _prescription.value.copy(
            medications = medsMap
        )
    }

    fun updateNotes(notes: String) {
        _prescription.value = _prescription.value.copy(notes = notes)
    }

    fun savePrescription(
        patientId: String,
        patientName: String,
        doctorId: String,
        doctorName: String,
        notes: String,
        medications: Map<String, Medication>,
        onDone: (Boolean) -> Unit
    ) {
        val newPrescription = Prescription(
            id = "",
            patientId = patientId,
            patientName = patientName,
            doctorId = doctorId,
            doctorName = doctorName,
            date = System.currentTimeMillis(),
            medications = medications,
            notes = notes,
            qrCodeUrl = ""
        )

        repository.createPrescription(newPrescription) { success, _ ->
            onDone(success)
        }
    }

    fun clearPrescription() {
        _selectedPatient.value = null
        _prescription.value = Prescription(medications = emptyMap())
        _medications.value = emptyList()
    }


}
