package com.fit2081.yeeun34752110.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.yeeun34752110.databases.patientdb.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.fit2081.yeeun34752110.databases.AppDataBase
import com.fit2081.yeeun34752110.databases.AuthManager

class AuthViewModel(private val repository: PatientRepository) : ViewModel() {

    // -------------------------------
    // UI State - General
    // -------------------------------
    var isLoading = mutableStateOf(false)
        private set

    fun setLoading(value: Boolean) {
        isLoading.value = value
    }

    val patientIds: Flow<List<Int>> = repository.allPatientIds()

    // -------------------------------
    // Login State
    // -------------------------------
    var selectedUserId by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var expanded by mutableStateOf(false)
        private set
    var loginSuccessful = mutableStateOf<Boolean?>(null)
        private set
    var loginMessage = mutableStateOf<String?>(null)
        private set

    fun selectedUserId(id: String) {
        selectedUserId = id
    }

    fun updatePassword(pw: String) {
        password = pw
    }

    fun toggleDropdown() {
        expanded = !expanded
    }

    fun dismissDropdown() {
        expanded = false
    }

    // -------------------------------
    // Registration State
    // -------------------------------
    var name by mutableStateOf("")
        private set
    var phone by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var registerDropdownExpanded by mutableStateOf(false)
        private set
    var registrationSuccessful = mutableStateOf(false)
        private set
    var registrationMessage = mutableStateOf<String?>(null)
        private set

    fun typeUserId(id: String) {
        selectedUserId = id
    }

    fun typeName(nane: String) {
        name = nane
    }

    fun typePhone(phoneNum: String) {
        phone = phoneNum
    }

    fun typeConfirmPassword(pw: String) {
        confirmPassword = pw
    }

    fun toggleRegisterDropdown() {
        registerDropdownExpanded = !registerDropdownExpanded
    }

    fun dismissRegisterDropdown() {
        registerDropdownExpanded = false
    }

    // -------------------------------
    // Login Logic
    // -------------------------------
    fun loginFun(userId: String, password: String, context: Context) {
        viewModelScope.launch {
            loginSuccessful.value = false

            val patientId = userId.toIntOrNull()
            if (patientId == null) {
                loginMessage.value = "Invalid ID format"
                return@launch
            }

            val patient = repository.getPatientById(patientId)
            when {
                patient == null -> {
                    loginMessage.value = "User ID not found"
                }
                patient.patientPassword.isEmpty() -> {
                    loginMessage.value = "Account has not been registered"
                }
                patient.patientPassword != password -> {
                    loginMessage.value = "Incorrect password"
                }
                else -> {
                    AuthManager.login(patientId.toString())

                    loginMessage.value = "Login successful!"
                    loginSuccessful.value = true
                }
            }
        }
    }

    // -------------------------------
    // Registration Logic
    // -------------------------------
    fun registerFun(
        selectedId: String,
        name: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        Log.d("AuthViewModel", "Attempting registration")

        viewModelScope.launch {
            registrationSuccessful.value = false

            val patientId = selectedId.toIntOrNull()
            if (patientId == null) {
                registrationMessage.value = "Invalid ID format."
                return@launch
            }

            val patient = repository.getPatientById(patientId)
            if (patient == null || patient.patientPhoneNumber != phone) {
                registrationMessage.value = "ID or phone number is incorrect."
                return@launch
            }

            // Name validation: exactly 8 Eng chars, not empty
            val namePattern = Regex("^[A-Za-z]{1,8}$")
            if (!namePattern.matches(name)) {
                registrationMessage.value = "Maximum 8 characters (A–Z, a–z)."
                return@launch
            }

            // Password validation: 4-8 characters, only letters and digits
            val passwordPattern = Regex("^[A-Za-z0-9]{4,8}\$")
            if (password.isBlank() || !passwordPattern.matches(password)) {
                registrationMessage.value = "Password must be 4-8 characters long with only letters and digits."
                return@launch
            }

            if (password != confirmPassword) {
                registrationMessage.value = "Passwords do not match."
                return@launch
            }

            val updated = patient.copy(
                patientName = name,
                patientPassword = password
            )

            repository.updatePatient(updated)
            registrationSuccessful.value = true
            registrationMessage.value = "Registration successful!"
        }
    }

    // -------------------------------
    // Validation
    // -------------------------------
    var destinationAfterLogin = mutableStateOf<String?>(null)
        private set

    fun handlePostLoginNavigation(context: Context) {
        viewModelScope.launch {
            val patientId = AuthManager.getPatientId()?.toIntOrNull()

            if (patientId == null || patientId == -1) {
                destinationAfterLogin.value = "login"
                return@launch
            }

            val dao = AppDataBase.getDatabase(context).foodIntakeDao()
            val intake = dao.getFoodIntakeByPatientId(patientId)

            val hasCompleted = intake != null && (
                    intake.intakeFruits || intake.intakeVegetables || intake.intakeGrains ||
                            intake.intakeRedMeat || intake.intakeSeafood || intake.intakePoultry ||
                            intake.intakeFish || intake.intakeEggs || intake.intakeNutsOrSeeds
                    )

            destinationAfterLogin.value = if (hasCompleted) "home" else "questionnaire"
        }
    }
}
