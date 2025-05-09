package com.fit2081.yeeun34752110.nutricoach.fruitapi

data class ResponseModel(
    val name: String,
    val family: String,
    val genus: String,
    val order: String,
    val nutritions: Nutrition
)

data class Nutrition(
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val calories: Double,
    val sugar: Double
)
