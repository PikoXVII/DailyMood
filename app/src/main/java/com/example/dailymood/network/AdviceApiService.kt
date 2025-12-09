package com.example.dailymood.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// JSON från https://api.adviceslip.com/advice ser ut ungefär så här:
// { "slip": { "id": 42, "advice": "Take time for yourself." } }

data class AdviceSlip(
    val id: Int,
    val advice: String
)

data class AdviceResponse(
    val slip: AdviceSlip
)

interface AdviceApi {
    @GET("advice")
    suspend fun getAdvice(): AdviceResponse
}

// Singleton som håller Retrofit-klienten
object AdviceApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.adviceslip.com/") // servern vi pratar med
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: AdviceApi = retrofit.create(AdviceApi::class.java)
}
