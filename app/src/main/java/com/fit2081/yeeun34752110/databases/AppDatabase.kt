package com.fit2081.yeeun34752110.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fit2081.yeeun34752110.databases.foodintakedb.FoodIntake
import com.fit2081.yeeun34752110.databases.foodintakedb.FoodIntakeDao
import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTips
import com.fit2081.yeeun34752110.databases.nutricoachtips.NutriCoachTipsDao
import com.fit2081.yeeun34752110.databases.patientdb.Patient
import com.fit2081.yeeun34752110.databases.patientdb.PatientDao

@Database(
    entities = [Patient::class, FoodIntake::class, NutriCoachTips::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun nutriCoachTipsDao(): NutriCoachTipsDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
