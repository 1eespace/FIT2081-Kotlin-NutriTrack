package com.fit2081.yeeun34752110.questionnaire

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.yeeun34752110.databases.foodintakedb.FoodIntake
import com.fit2081.yeeun34752110.databases.foodintakedb.FoodIntakeRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class FoodIntakeQuestionnaireViewModel (val repository: FoodIntakeRepository) : ViewModel() {

    // --- Food category selections ---
    var selectedCategories = mutableStateMapOf<String, Boolean>().apply {
        listOf(
            "Fruits", "Vegetables", "Grains", "Red Meat", "Seafood",
            "Poultry", "Fish", "Eggs", "Nuts/Seeds"
        ).forEach { put(it, false) }
    }
        private set

    // --- Persona and Dropdown ---
    var selectedPersona by mutableStateOf("Health Devotee")
        private set
    var showPersonaModal by mutableStateOf(false)
        private set
    var selectedModalPersona by mutableStateOf<String?>(null)
        private set
    var personaDropdownExpanded by mutableStateOf(false)
        private set

    // --- TimePickers ---
    var biggestMealTime by mutableStateOf("12:00")
        private set
    var sleepTime by mutableStateOf("23:00")
        private set
    var wakeTime by mutableStateOf("07:00")
        private set

    fun updateSelectedPersona(value: String) {
        selectedPersona = value
    }

    fun togglePersonaModal(show: Boolean, persona: String? = null) {
        showPersonaModal = show
        selectedModalPersona = persona
    }

    fun toggleDropdown(expand: Boolean) {
        personaDropdownExpanded = expand
    }

    fun updateMealTime(label: String, time: String) {
        when (label) {
            "meal" -> biggestMealTime = time
            "sleep" -> sleepTime = time
            "wake" -> wakeTime = time
        }
    }

    fun saveFoodIntake(
        userId: Int,
        selectedCategories: Map<String, Boolean>,
        biggestMealTime: String,
        sleepTime: String,
        wakeTime: String,
        selectedPersona: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (selectedCategories.values.none { it }) {
            onError("Please select at least one food category!")
            return
        }

        if (hasDuplicateTimes(biggestMealTime, sleepTime, wakeTime)) {
            onError("Meal, sleep, and wake times must all be different!")
            return
        }

        val intake = FoodIntake(
            patientId = userId,
            sleepTime = sleepTime,
            wakeTime = wakeTime,
            biggestMealTime = biggestMealTime,
            selectedPersona = selectedPersona,
            intakeFruits = selectedCategories["Fruits"] ?: false,
            intakeVegetables = selectedCategories["Vegetables"] ?: false,
            intakeGrains = selectedCategories["Grains"] ?: false,
            intakeRedMeat = selectedCategories["Red Meat"] ?: false,
            intakeSeafood = selectedCategories["Seafood"] ?: false,
            intakePoultry = selectedCategories["Poultry"] ?: false,
            intakeFish = selectedCategories["Fish"] ?: false,
            intakeEggs = selectedCategories["Eggs"] ?: false,
            intakeNutsOrSeeds = selectedCategories["Nuts/Seeds"] ?: false
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
                    sleepTime = it.sleepTime
                    wakeTime = it.wakeTime
                    biggestMealTime = it.biggestMealTime
                    selectedPersona = it.selectedPersona

                    selectedCategories["Fruits"] = it.intakeFruits
                    selectedCategories["Vegetables"] = it.intakeVegetables
                    selectedCategories["Grains"] = it.intakeGrains
                    selectedCategories["Red Meat"] = it.intakeRedMeat
                    selectedCategories["Seafood"] = it.intakeSeafood
                    selectedCategories["Poultry"] = it.intakePoultry
                    selectedCategories["Fish"] = it.intakeFish
                    selectedCategories["Eggs"] = it.intakeEggs
                    selectedCategories["Nuts/Seeds"] = it.intakeNutsOrSeeds

                    isDataLoaded = true
                }
            } catch (e: Exception) {
            }
        }
    }
}
