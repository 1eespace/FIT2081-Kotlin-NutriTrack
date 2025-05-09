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

    fun loadAndInsertData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE)
        val isDataLoaded = sharedPreferences.getBoolean("isDataLoaded", false)

        // If data has already been loaded, return early
        if (isDataLoaded) {
            Log.v("MainViewModel", "Data already loaded. Skipping CSV insertion.")
            return
        }

        Log.v("MainViewModel", "Getting csv data")
        val currentDate = Date()
        println("Current Date object: $currentDate")
        viewModelScope.launch(Dispatchers.IO) {

            val inputStream = context.assets.open("user_data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines()


            for (line in lines.drop(1)) {
                val tokens = line.split(",")
                if (tokens.size < 62) continue

                val phone = tokens[0]
                val patientId = tokens[1].trim().toInt()
                val name = ""
                val sex = tokens[2]
                val password = ""
                val isMale = sex.equals("Male", ignoreCase = true)

                val patient = Patient(
                    patientId = patientId,
                    patientName = name,
                    patientSex = sex,
                    patientPassword = password,
                    patientPhoneNumber = phone,
                    vegetables = tokens[if (isMale) 8 else 9].toFloat(),
                    fruits = tokens[if (isMale) 19 else 20].toFloat(),
                    grainsAndCereals = tokens[if (isMale) 29 else 30].toFloat(),
                    wholeGrains = tokens[if (isMale) 33 else 34].toFloat(),
                    meatAndAlternatives = tokens[if (isMale) 36 else 37].toFloat(),
                    dairyAndAlternatives = tokens[if (isMale) 40 else 41].toFloat(),
                    water = tokens[if (isMale) 49 else 50].toFloat(),
                    saturatedFats = tokens[if (isMale) 57 else 58].toFloat(),
                    unsaturatedFats = tokens[if (isMale) 60 else 61].toFloat(),
                    sodium = tokens[if (isMale) 43 else 44].toFloat(),
                    sugars = tokens[if (isMale) 54 else 55].toFloat(),
                    alcohol = tokens[if (isMale) 46 else 47].toFloat(),
                    discretionaryFoods = tokens[if (isMale) 5 else 6].toFloat(),
                    totalScore = tokens[if (isMale) 3 else 4].toFloat()
                )

                repository.safeInsert(patient)
            }

            Log.d("MainViewModel", "All patients reloaded successfully")
        }
    }
}


