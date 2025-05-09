package com.fit2081.yeeun34752110

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fit2081.yeeun34752110.auth.AuthViewModel
import com.fit2081.yeeun34752110.clinician.ClinicianViewModel
import com.fit2081.yeeun34752110.databases.AppDataBase
import com.fit2081.yeeun34752110.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTipsRepository
import com.fit2081.yeeun34752110.databases.patientdb.PatientRepository
import com.fit2081.yeeun34752110.home.HomeViewModel
import com.fit2081.yeeun34752110.insights.InsightsViewModel
import com.fit2081.yeeun34752110.nutricoach.NutriCoachViewModel
import com.fit2081.yeeun34752110.genai.GenAiViewModel
import com.fit2081.yeeun34752110.questionnaire.FoodIntakeQuestionnaireViewModel
import com.fit2081.yeeun34752110.settings.SettingsViewModel

class AppViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {
    private val db = AppDataBase.getDatabase(context)
    private val patientRepository = PatientRepository(db.patientDao())
    private val foodIntakeRepository = FoodIntakeRepository(db.foodIntakeDao())
    private val nutriCoachTipsRepository = NutriCoachTipsRepository(db.nutriCoachTipsDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(patientRepository) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(patientRepository) as T
            }

            modelClass.isAssignableFrom(FoodIntakeQuestionnaireViewModel::class.java) -> {
                FoodIntakeQuestionnaireViewModel(foodIntakeRepository) as T
            }

            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                InsightsViewModel(patientRepository) as T
            }

            modelClass.isAssignableFrom(NutriCoachViewModel::class.java) -> {
                // ✅ 여기를 수정: 두 개의 repository를 전달
                NutriCoachViewModel(patientRepository, nutriCoachTipsRepository) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(patientRepository) as T
            }

            modelClass.isAssignableFrom(ClinicianViewModel::class.java) -> {
                ClinicianViewModel(patientRepository) as T
            }

            modelClass.isAssignableFrom(GenAiViewModel::class.java) -> {
                GenAiViewModel(nutriCoachTipsRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
