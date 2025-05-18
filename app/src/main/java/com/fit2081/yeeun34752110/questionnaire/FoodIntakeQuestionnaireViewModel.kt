package com.fit2081.yeeun34752110.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.yeeun34752110.databases.AuthManager
import com.fit2081.yeeun34752110.databases.foodintakedb.FoodIntake
import com.fit2081.yeeun34752110.databases.foodintakedb.FoodIntakeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FoodIntakeQuestionnaireViewModel(val repository: FoodIntakeRepository) : ViewModel() {

    // --- Food category selections ---
    private val _selectedCategories = MutableStateFlow(
        listOf(
            "Fruits", "Vegetables", "Grains", "Red Meat", "Seafood",
            "Poultry", "Fish", "Eggs", "Nuts/Seeds"
        ).associateWith { false }
    )
    val selectedCategories: StateFlow<Map<String, Boolean>> = _selectedCategories.asStateFlow()

    fun toggleCategory(category: String, isChecked: Boolean) {
        _selectedCategories.value = _selectedCategories.value.toMutableMap().apply {
            this[category] = isChecked
        }
    }

    // --- Persona and Dropdown ---
    private val _selectedPersona = MutableStateFlow("Health Devotee")
    val selectedPersona: StateFlow<String> = _selectedPersona.asStateFlow()

    private val _showPersonaModal = MutableStateFlow(false)
    val showPersonaModal: StateFlow<Boolean> = _showPersonaModal.asStateFlow()

    private val _selectedModalPersona = MutableStateFlow<String?>(null)
    val selectedModalPersona: StateFlow<String?> = _selectedModalPersona.asStateFlow()

    private val _personaDropdownExpanded = MutableStateFlow(false)
    val personaDropdownExpanded: StateFlow<Boolean> = _personaDropdownExpanded.asStateFlow()

    fun updateSelectedPersona(value: String) {
        _selectedPersona.value = value
    }

    fun togglePersonaModal(show: Boolean, persona: String? = null) {
        _showPersonaModal.value = show
        _selectedModalPersona.value = persona
    }

    fun toggleDropdown(expand: Boolean) {
        _personaDropdownExpanded.value = expand
    }

    // --- TimePickers ---
    private val _biggestMealTime = MutableStateFlow("12:00")
    val biggestMealTime: StateFlow<String> = _biggestMealTime.asStateFlow()

    private val _sleepTime = MutableStateFlow("23:00")
    val sleepTime: StateFlow<String> = _sleepTime.asStateFlow()

    private val _wakeTime = MutableStateFlow("07:00")
    val wakeTime: StateFlow<String> = _wakeTime.asStateFlow()

    fun updateMealTime(label: String, time: String) {
        when (label) {
            "meal" -> _biggestMealTime.value = time
            "sleep" -> _sleepTime.value = time
            "wake" -> _wakeTime.value = time
        }
    }

    fun saveFoodIntake(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val selectedCategoriesMap = selectedCategories.value
        val meal = biggestMealTime.value
        val sleep = sleepTime.value
        val wake = wakeTime.value
        val persona = selectedPersona.value

        if (selectedCategoriesMap.values.none { it }) {
            onError("Please select at least one food category!")
            return
        }

        if (hasDuplicateTimes(meal, sleep, wake)) {
            onError("Meal, sleep, and wake times must all be different!")
            return
        }

        val intake = FoodIntake(
            patientId = AuthManager.getPatientId()?.toIntOrNull() ?: -1,
            sleepTime = sleep,
            wakeTime = wake,
            biggestMealTime = meal,
            selectedPersona = persona,
            intakeFruits = selectedCategoriesMap["Fruits"] ?: false,
            intakeVegetables = selectedCategoriesMap["Vegetables"] ?: false,
            intakeGrains = selectedCategoriesMap["Grains"] ?: false,
            intakeRedMeat = selectedCategoriesMap["Red Meat"] ?: false,
            intakeSeafood = selectedCategoriesMap["Seafood"] ?: false,
            intakePoultry = selectedCategoriesMap["Poultry"] ?: false,
            intakeFish = selectedCategoriesMap["Fish"] ?: false,
            intakeEggs = selectedCategoriesMap["Eggs"] ?: false,
            intakeNutsOrSeeds = selectedCategoriesMap["Nuts/Seeds"] ?: false
        )

        viewModelScope.launch {
            repository.insertFoodIntake(intake)
            onSuccess()
        }
    }

    private var isDataLoaded = false

    fun loadExistingFoodIntake(patientId: Int) {
        if (isDataLoaded) return

        viewModelScope.launch {
            try {
                val intake = repository.getFoodIntakeByPatientId(patientId)
                intake?.let {
                    _sleepTime.value = it.sleepTime
                    _wakeTime.value = it.wakeTime
                    _biggestMealTime.value = it.biggestMealTime
                    _selectedPersona.value = it.selectedPersona

                    _selectedCategories.value = mapOf(
                        "Fruits" to it.intakeFruits,
                        "Vegetables" to it.intakeVegetables,
                        "Grains" to it.intakeGrains,
                        "Red Meat" to it.intakeRedMeat,
                        "Seafood" to it.intakeSeafood,
                        "Poultry" to it.intakePoultry,
                        "Fish" to it.intakeFish,
                        "Eggs" to it.intakeEggs,
                        "Nuts/Seeds" to it.intakeNutsOrSeeds
                    )

                    isDataLoaded = true
                }
            } catch (e: Exception) {
                // Error handling (e.g., logging) if needed
            }
        }
    }
}
