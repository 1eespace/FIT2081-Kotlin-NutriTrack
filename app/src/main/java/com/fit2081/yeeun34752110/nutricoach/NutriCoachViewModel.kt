package com.fit2081.yeeun34752110.nutricoach

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTips
import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTipsRepository
import com.fit2081.yeeun34752110.databases.patientdb.Patient
import com.fit2081.yeeun34752110.databases.patientdb.PatientRepository
import com.fit2081.yeeun34752110.nutricoach.fruitapi.FruitsRepository
import com.fit2081.yeeun34752110.nutricoach.fruitapi.ResponseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class NutriCoachViewModel(
    private val patientRepository: PatientRepository,
    private val tipsRepository: NutriCoachTipsRepository
) : ViewModel() {

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    var fruitName by mutableStateOf("")
        private set

    var fruitDetailsMap by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    var fetchedFruit: ResponseModel? = null
        private set

    fun updateFruitName(newValue: String) {
        fruitName = newValue
    }

    fun clearFruitDetails() {
        fruitName = ""
        fruitDetailsMap = emptyMap()
    }

    suspend fun fetchFruitDetails(
        trimmed: String,
        decimalFormat: DecimalFormat,
        fruitRepo: FruitsRepository
    ) {
        val result = fruitRepo.getFruitDetails(trimmed)
        result?.let {
            fetchedFruit = it
            fruitDetailsMap = mapOf(
                "Family" to it.family,
                "Calories" to decimalFormat.format(it.nutritions.calories),
                "Fat" to decimalFormat.format(it.nutritions.fat),
                "Sugar" to decimalFormat.format(it.nutritions.sugar),
                "Carbohydrates" to decimalFormat.format(it.nutritions.carbohydrates),
                "Protein" to decimalFormat.format(it.nutritions.protein)
            )
        }
    }

    // AI Tips
    var tipList = mutableStateListOf<NutriCoachTips>()
        private set

    var showDialog by mutableStateOf(false)
        private set

    fun toggleDialog(show: Boolean) {
        showDialog = show
    }

    suspend fun loadTipsIfNeeded(patientId: Int) {
        if (showDialog) {
            val tips = tipsRepository.getTipsForPatient(patientId)
            tipList.clear()
            tipList.addAll(tips)
        }
    }

    fun loadPatientScoresById(id: Int) {
        viewModelScope.launch {
            _patient.value = patientRepository.getPatientById(id)
        }
    }
}

//package com.fit2081.yeeun34752110.nutricoach
//
//import androidx.compose.runtime.*
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTips
//import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTipsRepository
//import com.fit2081.yeeun34752110.databases.patientdb.Patient
//import com.fit2081.yeeun34752110.databases.patientdb.PatientRepository
//import com.fit2081.yeeun34752110.nutricoach.fruitapi.FruitsRepository
//import com.fit2081.yeeun34752110.nutricoach.fruitapi.ResponseModel
//import kotlinx.coroutines.launch
//import java.text.DecimalFormat
//
//class NutriCoachViewModel(
//    private val patientRepository: PatientRepository,
//    private val tipsRepository: NutriCoachTipsRepository
//) : ViewModel() {
//
//    private val _patient = MutableLiveData<Patient?>()
//    val patient: LiveData<Patient?> = _patient
//
//    var fruitName by mutableStateOf("")
//        private set
//
//    var fruitDetailsMap by mutableStateOf<Map<String, String>>(emptyMap())
//        private set
//
//    var fetchedFruit: ResponseModel? = null
//        private set
//
//    fun updateFruitName(newValue: String) {
//        fruitName = newValue
//    }
//
//    fun clearFruitDetails() {
//        fruitName = ""
//        fruitDetailsMap = emptyMap()
//    }
//
//    suspend fun fetchFruitDetails(
//        trimmed: String,
//        decimalFormat: DecimalFormat,
//        fruitRepo: FruitsRepository
//    ) {
//        val result = fruitRepo.getFruitDetails(trimmed)
//        result?.let {
//            fetchedFruit = it
//            fruitDetailsMap = mapOf(
//                "Family" to it.family,
//                "Calories" to decimalFormat.format(it.nutritions.calories),
//                "Fat" to decimalFormat.format(it.nutritions.fat),
//                "Sugar" to decimalFormat.format(it.nutritions.sugar),
//                "Carbohydrates" to decimalFormat.format(it.nutritions.carbohydrates),
//                "Protein" to decimalFormat.format(it.nutritions.protein)
//            )
//        }
//    }
//
//    // AI Tips
//    var tipList = mutableStateListOf<NutriCoachTips>()
//        private set
//
//    var showDialog by mutableStateOf(false)
//        private set
//
//    fun toggleDialog(show: Boolean) {
//        showDialog = show
//    }
//
//    suspend fun loadTipsIfNeeded(patientId: Int) {
//        if (showDialog) {
//            val tips = tipsRepository.getTipsForPatient(patientId)
//            tipList.clear()
//            tipList.addAll(tips)
//        }
//    }
//
//    fun loadPatientScoresById(id: Int) {
//        viewModelScope.launch {
//            _patient.value = patientRepository.getPatientById(id)
//        }
//    }
//}
