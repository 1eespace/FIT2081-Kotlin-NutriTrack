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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val patientDao = AppDataBase.getDatabase(application).patientDao()
    private val repository = PatientRepository(patientDao)

    fun loadAndInsertFromCSV(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Check if database already contains any patients
                val existingPatients = repository.getAllPatientsOnce()
                if (existingPatients.isNotEmpty()) {
                    Log.d("MainViewModel", "CSV already loaded. Skipping import.")
                    return@launch
                }

                val inputStream = context.assets.open("user_data.csv")
                val reader = BufferedReader(InputStreamReader(inputStream))
                val lines = reader.readLines()
                if (lines.isEmpty()) return@launch

                // Parse CSV headers
                val headers = lines.first().split(",").map { it.trim() }
                val headerIndex = headers.withIndex().associate { it.value to it.index }

                // Iterate through CSV lines and insert patients
                for (line in lines.drop(1)) {
                    val tokens = line.split(",").map { it.trim() }
                    if (tokens.size < headers.size) continue

                    val phone = tokens[headerIndex["PhoneNumber"] ?: continue]
                    val patientId = tokens[headerIndex["User_ID"] ?: continue].toIntOrNull() ?: continue
                    val sex = tokens[headerIndex["Sex"] ?: continue]
                    val gender = if (sex.equals("Male", ignoreCase = true)) "Male" else "Female"

                    // Skip if this patient already exists in the database
                    val existing = repository.getPatientById(patientId)
                    if (existing != null) {
                        Log.d("MainViewModel", "Patient ID $patientId already exists. Skipping.")
                        continue
                    }

                    // Retrieve HEIFA score based on gendered column
                    fun getFloat(prefix: String): Float {
                        val columnName = "${prefix}$gender"
                        val index = headerIndex[columnName] ?: return 0f
                        return tokens[index].toFloatOrNull() ?: 0f
                    }

                    // Create Patient object from CSV row
                    val patient = Patient(
                        patientId = patientId,
                        patientName = "",
                        patientSex = sex,
                        patientPassword = "",
                        patientPhoneNumber = phone,
                        totalScore = getFloat("HEIFAtotalscore"),
                        discretionaryFoods = getFloat("DiscretionaryHEIFAscore"),
                        vegetables = getFloat("VegetablesHEIFAscore"),
                        fruits = getFloat("FruitHEIFAscore"),
                        grainsAndCereals = getFloat("GrainsandcerealsHEIFAscore"),
                        wholeGrains = getFloat("WholegrainsHEIFAscore"),
                        meatAndAlternatives = getFloat("MeatandalternativesHEIFAscore"),
                        dairyAndAlternatives = getFloat("DairyandalternativesHEIFAscore"),
                        water = getFloat("WaterHEIFA"),
                        saturatedFats = getFloat("SaturatedFatHEIFAscore"),
                        unsaturatedFats = getFloat("UnsaturatedFatHEIFAscore"),
                        sodium = getFloat("SodiumHEIFAscore"),
                        sugars = getFloat("SugarHEIFAscore"),
                        alcohol = getFloat("AlcoholHEIFAscore")
                    )

                    // Insert new patient into the database
                    repository.dataInsert(patient)
                }

                Log.d("MainViewModel", "CSV loaded and inserted successfully.")

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading CSV", e)
            }
        }
    }
}
