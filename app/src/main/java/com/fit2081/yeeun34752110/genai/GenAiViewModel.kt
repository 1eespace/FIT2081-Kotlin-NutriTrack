package com.fit2081.yeeun34752110.genai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.fit2081.yeeun34752110.BuildConfig
import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTips
import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTipsRepository

class GenAiViewModel(
    private val repository: NutriCoachTipsRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GENAI_API_KEY
    )

    suspend fun getTips(patientId: Int): List<NutriCoachTips> {
        return repository.getTipsForPatient(patientId)
    }

    fun sendPrompt(
        prompt: String,
        patientId: Int
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    // Update UI
                    _uiState.value = UiState.Success(outputContent)

                    // Only save if patientId is valid
                    if (patientId != -1) {
                        repository.insertTip(
                            NutriCoachTips(patientId = patientId, message = outputContent)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}