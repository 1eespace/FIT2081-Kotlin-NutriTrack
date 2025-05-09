package com.fit2081.yeeun34752110.nutricoach.fruitapi

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {
    @GET("api/fruit/{name}")
    suspend fun getFruitDetails(@Path("name") name: String): Response<ResponseModel>

    companion object {
        private const val BASE_URL = "https://www.fruityvice.com"

        fun create(): APIService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(APIService::class.java)
        }
    }
}
