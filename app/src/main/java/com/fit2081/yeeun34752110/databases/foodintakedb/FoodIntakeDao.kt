package com.fit2081.yeeun34752110.databases.foodintakedb

import androidx.room.*


@Dao
interface FoodIntakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intake WHERE patientId = :id LIMIT 1")
    suspend fun getFoodIntakeByPatientId(id: Int): FoodIntake?

}

