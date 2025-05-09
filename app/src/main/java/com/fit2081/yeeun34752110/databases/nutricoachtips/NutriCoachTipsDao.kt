package com.fit2081.yeeun34752110.databases.nutricoachtips

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NutriCoachTipsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: NutriCoachTips)

    @Query("SELECT * FROM nutricoach_tips WHERE patientId = :patientId")
    suspend fun getTipsForPatient(patientId: Int): List<NutriCoachTips>
}
