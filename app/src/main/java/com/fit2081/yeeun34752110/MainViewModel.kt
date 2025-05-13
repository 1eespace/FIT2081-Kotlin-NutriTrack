package com.fit2081.yeeun34752110

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.yeeun34752110.databases.AppDataBase
import com.fit2081.yeeun34752110.databases.patientdb.Patient
import com.fit2081.yeeun34752110.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val patientDao = AppDataBase.getDatabase(application).patientDao()
    private val repository = PatientRepository(patientDao)

    fun loadAndInsertFromCSV(context: Context) {
        val sharedPreferences = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE)
        val isDataLoaded = sharedPreferences.getBoolean("isDataLoaded", false)

        if (isDataLoaded) {
            Log.v("MainViewModel", "Data already loaded. There is no CSV insertion.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("user_data.csv")
                val reader = BufferedReader(InputStreamReader(inputStream))
                val lines = reader.readLines()
                if (lines.isEmpty()) return@launch

                val headers = lines.first().split(",").map { it.trim() }
                val headerIndex = headers.withIndex().associate { it.value to it.index }

                for (line in lines.drop(1)) {
                    val tokens = line.split(",")
                    if (tokens.size < headers.size) continue

                    val phone = tokens[headerIndex["Phone"] ?: continue].trim()
                    val patientId = tokens[headerIndex["PatientID"] ?: continue].trim().toInt()
                    val sex = tokens[headerIndex["Sex"] ?: continue].trim()
                    val isMale = sex.equals("Male", ignoreCase = true)
                    val genderKey = if (isMale) "Male" else "Female"

                    // Get values
                    fun get(field: String): Float {
                        val columnName = "${field}_$genderKey"
                        val index = headerIndex[columnName] ?: error("Missing column: $columnName")
                        return tokens[index].toFloat()
                    }

                    val patient = Patient(
                        patientId = patientId,
                        patientName = "",
                        patientSex = sex,
                        patientPassword = "",
                        patientPhoneNumber = phone,
                        vegetables = get("Vegetables"),
                        fruits = get("Fruits"),
                        grainsAndCereals = get("GrainsAndCereals"),
                        wholeGrains = get("WholeGrains"),
                        meatAndAlternatives = get("MeatAndAlternatives"),
                        dairyAndAlternatives = get("DairyAndAlternatives"),
                        water = get("Water"),
                        saturatedFats = get("SaturatedFats"),
                        unsaturatedFats = get("UnsaturatedFats"),
                        sodium = get("Sodium"),
                        sugars = get("Sugars"),
                        alcohol = get("Alcohol"),
                        discretionaryFoods = get("DiscretionaryFoods"),
                        totalScore = get("TotalScore")
                    )

                    repository.safeInsert(patient)
                }

                Log.d("MainViewModel", "All patients reloaded successfully")

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error reading CSV: ${e.message}", e)
            }
        }
    }
}
