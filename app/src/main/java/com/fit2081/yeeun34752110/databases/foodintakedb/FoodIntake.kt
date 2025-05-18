package com.fit2081.yeeun34752110.databases.foodintakedb

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fit2081.yeeun34752110.databases.patientdb.Patient

@Entity(
    tableName = "food_intake",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["patientId"])]
)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true) val patientId: Int,

    var sleepTime: String = "",
    var wakeTime: String = "",
    var biggestMealTime: String = "",
    var selectedPersona: String = "",

    // Food category checkboxes
    var intakeFruits: Boolean = false,
    var intakeVegetables: Boolean = false,
    var intakeGrains: Boolean = false,
    var intakeRedMeat: Boolean = false,
    var intakeSeafood: Boolean = false,
    var intakePoultry: Boolean = false,
    var intakeFish: Boolean = false,
    var intakeEggs: Boolean = false,
    var intakeNutsOrSeeds: Boolean = false


)
