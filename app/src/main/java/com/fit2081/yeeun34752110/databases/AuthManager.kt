package com.fit2081.yeeun34752110.databases

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState

// Using SharedPref for maintaining login session before logout
object AuthManager {
    private const val SHARED_PREF = "auth_manager"
    private const val KEY_PATIENT_ID = "patient_id"

    val _patientId: MutableState<String?> = mutableStateOf(null)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        _patientId.value = prefs.getString(KEY_PATIENT_ID, null)
    }

    fun login(context: Context, patientId: String) {
        _patientId.value = patientId
        val prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PATIENT_ID, patientId).apply()
    }

    fun logout(context: Context) {
        _patientId.value = null
        val prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_PATIENT_ID).apply()
    }

    fun getPatientId(): String? {
        return _patientId.value
    }
}
