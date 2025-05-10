package com.fit2081.yeeun34752110.databases.patientdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val patientId: Int,
    var patientSex: String,
    var patientPhoneNumber: String,
    var patientName: String,
    var patientPassword: String,

    // Dietary category
    val totalScore: Float,
    val discretionaryFoods: Float,
    val vegetables: Float,
    val fruits: Float,
    val grainsAndCereals: Float,
    val wholeGrains: Float,
    val meatAndAlternatives: Float,
    val dairyAndAlternatives: Float,
    val water: Float,
    val saturatedFats: Float,
    val unsaturatedFats: Float,
    val sodium: Float,
    val sugars: Float,
    val alcohol: Float
)