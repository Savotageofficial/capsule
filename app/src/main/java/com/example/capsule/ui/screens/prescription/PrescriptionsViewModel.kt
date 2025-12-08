package com.example.capsule.ui.screens.prescription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capsule.data.model.*
import com.example.capsule.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class PrescriptionViewModel : ViewModel() {
    private val repository = ProfileRepository.getInstance()
    private val TAG = "PrescriptionViewModel"

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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        Log.d(TAG, "ViewModel initialized")
        loadCurrentDoctor()
    }

    fun loadCurrentDoctor() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Loading current doctor")
            repository.getCurrentDoctor { doctor ->
                Log.d(TAG, "Doctor loaded: ${doctor?.name ?: "null"}")
                _doctor.value = doctor
                _isLoading.value = false
            }
        }
    }

    fun loadDoctorAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Loading doctor appointments")
            val currentDoctor = _doctor.value
            if (currentDoctor != null && currentDoctor.id.isNotBlank()) {
                repository.getDoctorAppointments(currentDoctor.id) { appointments ->
                    Log.d(TAG, "Appointments loaded: ${appointments.size}")
                    _appointments.value = appointments
                    _isLoading.value = false
                }
            } else {
                Log.d(TAG, "Doctor not loaded yet, loading doctor first")
                repository.getCurrentDoctor { doctor ->
                    _doctor.value = doctor
                    val docId = doctor?.id
                    if (!docId.isNullOrBlank()) {
                        repository.getDoctorAppointments(docId) { appointments ->
                            _appointments.value = appointments
                            _isLoading.value = false
                        }
                    } else {
                        Log.w(TAG, "Doctor ID is null or blank")
                        _appointments.value = emptyList()
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun selectPatient(appointment: Appointment) {
        Log.d(TAG, "Selecting patient: ${appointment.patientName}")
        _selectedPatient.value = appointment
        // Initialize prescription with patient info
        _prescription.value = _prescription.value.copy(
            patientId = appointment.patientId,
            patientName = appointment.patientName,
            doctorId = _doctor.value?.id ?: "",
            doctorName = _doctor.value?.name ?: ""
        )
        Log.d(TAG, "Prescription initialized for patient: ${appointment.patientName}")
    }

    // Medication management
    fun addMedication() {
        Log.d(TAG, "Adding new medication")
        val newList = _medications.value.toMutableList()
        newList.add(Medication())
        _medications.value = newList
        updatePrescriptionFromMedications()
    }

    fun updateMedication(index: Int, medication: Medication) {
        Log.d(TAG, "Updating medication at index $index")
        val newList = _medications.value.toMutableList()
        if (index in newList.indices) {
            newList[index] = medication
            _medications.value = newList
            updatePrescriptionFromMedications()
        } else {
            Log.e(TAG, "Index $index out of bounds for medications list")
        }
    }

    fun removeMedication(index: Int) {
        Log.d(TAG, "Removing medication at index $index")
        val newList = _medications.value.toMutableList()
        if (index in newList.indices) {
            newList.removeAt(index)
            _medications.value = newList
            updatePrescriptionFromMedications()
        } else {
            Log.e(TAG, "Index $index out of bounds for medications list")
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
        Log.d(TAG, "Saving prescription for patient: $patientName")
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
            Log.d(TAG, "Prescription save result: $success")
            onDone(success)
        }
    }

    fun clearPrescription() {
        Log.d(TAG, "Clearing prescription")
        _selectedPatient.value = null
        _prescription.value = Prescription(medications = emptyMap())
        _medications.value = emptyList()
    }

    fun setPatientForDirectPrescription(patientId: String, patientName: String) {
        Log.d(TAG, "Setting direct prescription for patient: $patientName (ID: $patientId)")

        viewModelScope.launch {
            try {
                _errorMessage.value = null

                // Get current doctor info
                val currentDoctor = _doctor.value
                Log.d(TAG, "Current doctor: ${currentDoctor?.name ?: "null"}")

                // If doctor is not loaded yet, wait a bit
                if (currentDoctor == null) {
                    Log.d(TAG, "Doctor not loaded yet, waiting...")
                    // Try to load doctor if not already loading
                    if (!_isLoading.value) {
                        loadCurrentDoctor()
                    }

                    // Wait for doctor to load (with timeout)
                    var waitCount = 0
                    while (_doctor.value == null && waitCount < 20) {
                        delay(100)
                        waitCount++
                        Log.d(TAG, "Waiting for doctor... attempt $waitCount")
                    }
                }

                val finalDoctor = _doctor.value
                val doctorId = finalDoctor?.id ?: "unknown_doctor"
                val doctorName = finalDoctor?.name ?: "Doctor"

                Log.d(TAG, "Creating mock appointment with doctor: $doctorName")

                // Create mock appointment
                val mockAppointment = Appointment(
                    id = "direct_${System.currentTimeMillis()}",
                    patientId = patientId,
                    patientName = patientName,
                    doctorId = doctorId,
                    doctorName = doctorName,
                    dateTime = System.currentTimeMillis(),
                    type = "Direct Prescription",
                    status = "Completed"
                )

                _selectedPatient.value = mockAppointment
                Log.d(TAG, "Selected patient set: ${mockAppointment.patientName}")

                // Initialize prescription with safe defaults
                _prescription.value = Prescription(
                    patientId = patientId,
                    patientName = patientName,
                    doctorId = doctorId,
                    doctorName = doctorName,
                    medications = emptyMap()
                )

                Log.d(TAG, "Prescription initialized")

                // Clear medications list
                _medications.value = emptyList()
                Log.d(TAG, "Medications list cleared")

            } catch (e: Exception) {
                Log.e(TAG, "Error setting direct prescription: ${e.message}", e)
                _errorMessage.value = "Failed to setup prescription: ${e.localizedMessage}"
            }
        }
    }
}