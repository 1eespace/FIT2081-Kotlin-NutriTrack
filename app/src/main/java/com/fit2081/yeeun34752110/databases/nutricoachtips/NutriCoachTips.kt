package com.fit2081.yeeun34752110.databases.nutricoachtips

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fit2081.yeeun34752110.databases.patientdb.Patient

@Entity(
    tableName = "nutricoach_tips",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["patientId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class NutriCoachTips(
    @PrimaryKey(autoGenerate = true) val tipsId: Int = 0,
    val patientId: Int,
    val message: String
)