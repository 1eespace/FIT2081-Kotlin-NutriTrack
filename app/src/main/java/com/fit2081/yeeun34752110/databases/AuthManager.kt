package com.fit2081.yeeun34752110.databases

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object AuthManager {
    val _patientId: MutableState<String?> = mutableStateOf(null)

    fun login(patientId: String) {
        _patientId.value = patientId
    }

    fun logout() {
        _patientId.value = null
    }

    fun getPatientId(): String? {
        return _patientId.value
    }
}
