package com.fit2081.yeeun34752110.nutricoach.fruitapi

class FruitsRepository {
    private val apiService = APIService.create()

    suspend fun getFruitDetails(name: String): ResponseModel? {
        return apiService.getFruitDetails(name).body()
    }
}
