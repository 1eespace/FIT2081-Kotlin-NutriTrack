package com.fit2081.yeeun34752110.databases.foodintakedb

class FoodIntakeRepository(private val foodIntakeDao: FoodIntakeDao) {

    suspend fun insertFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.insert(foodIntake)
    }

    suspend fun getFoodIntakeByPatientId(userId: Int): FoodIntake? {
        return foodIntakeDao.getFoodIntakeByPatientId(userId)
    }

}
