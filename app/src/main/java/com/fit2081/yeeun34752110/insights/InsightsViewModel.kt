package com.fit2081.yeeun34752110.insights

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.yeeun34752110.databases.patientdb.Patient
import com.fit2081.yeeun34752110.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InsightsViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _fruitsScore = MutableStateFlow(0f)
    val fruitsScore: StateFlow<Float> = _fruitsScore

    fun loadPatientScoresById(id: Int) {
        viewModelScope.launch {
            val patientData = repository.getPatientById(id)
            _patient.value = patientData
            _fruitsScore.value = patientData?.fruits ?: 0f
        }
    }

    // Using slider to change the fruit score
    fun onFruitsScoreChange(newScore: Float) {
        _fruitsScore.value = newScore
    }

    // Just update FruitScore (no Effect on totalScore)
    fun updateFruitsScore(patientId: Int, newFruitsScore: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFruitsScore(patientId, newFruitsScore)
            _patient.value = repository.getPatientById(patientId)
        }
    }

    // Share data; message format
    fun sharingInsights(context: Context, userScores: Map<String, Float>, totalScore: Float, maxScore: Float) {
        val shareText = buildString {
            append("🥗Nutritional Insights\n\n")
            userScores.forEach { (category, score) ->
                append("$category: %.2f/10\n".format(score))
            }
            append(
                "\n✨ Overall Food Quality Score: %.2f / %.0f\n".format(totalScore, maxScore)
            )
            append("Keep up the great work and continue striving for balance!🌱")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val chooser = Intent.createChooser(intent, "Share your insights via:")
        context.startActivity(chooser)
    }
}
