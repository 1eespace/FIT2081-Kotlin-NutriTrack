package com.fit2081.yeeun34752110.databases.nutricoachtips

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutricoach_tips")
data class NutriCoachTips(
    @PrimaryKey(autoGenerate = true) val tipsId: Int = 0,
    val patientId: Int,
    val message: String
)
