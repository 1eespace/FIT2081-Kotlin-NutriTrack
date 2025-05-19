package com.fit2081.yeeun34752110.clinician

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fit2081.yeeun34752110.databases.patientdb.Patient
import com.fit2081.yeeun34752110.databases.patientdb.PatientRepository
import com.fit2081.yeeun34752110.genai.GenAiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ClinicianKey
const val CLINICIAN_KEY= "dollar-entry-apples"

class ClinicianViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    // --- Clinician Key State ---
    var clinicianKey by mutableStateOf("")
        private set

    // --- Rotate Screen ---
    fun updateClinicianKey(newKey: String) {
        clinicianKey = newKey
    }

    fun clinicianLogin(): Boolean {
        return clinicianKey == CLINICIAN_KEY
    }

    // --- Avg Score ---
    private val _avgMaleScore = MutableStateFlow(0f)
    val avgMaleScore: StateFlow<Float> = _avgMaleScore

    private val _avgFemaleScore = MutableStateFlow(0f)
    val avgFemaleScore: StateFlow<Float> = _avgFemaleScore

    // --- GenerateAvgScore ---
    suspend fun generateAvgScores() {
        val patients = repository.getAllPatientsOnce()

        val malePatients = patients.filter { it.patientSex.equals("male", ignoreCase = true) }
        val femalePatients = patients.filter { it.patientSex.equals("female", ignoreCase = true) }

        val maleScore = if (malePatients.isNotEmpty()) {
            malePatients.map { it.totalScore }.average().toFloat()
        } else 0f

        val femaleScore = if (femalePatients.isNotEmpty()) {
            femalePatients.map { it.totalScore }.average().toFloat()
        } else 0f

        _avgMaleScore.value = maleScore
        _avgFemaleScore.value = femaleScore
    }

    // --- GenAi DataPatterns State ---
    private val _dataPatterns = MutableStateFlow<List<String>>(emptyList())
    val dataPatterns: StateFlow<List<String>> = _dataPatterns

    fun handleAiResponse(aiText: String) {
        _dataPatterns.value = aiText.split("\n").filter { it.isNotBlank() }.take(3)
    }

    // Refresh the dataPatterns
    fun clearDataPatterns() {
        _dataPatterns.value = emptyList()
    }

    suspend fun generateAiPatterns(genAiViewModel: GenAiViewModel) {
        val patients = repository.getAllPatientsOnce()
        val prompt = PatternsPromptTemplates.getRandomPrompt(patients)
        genAiViewModel.sendPrompt(prompt, patientId = -1) // No need to store this
    }

}
