package com.payflow.app.data.remote

import retrofit2.http.GET

interface ExchangeRateService {
    @GET("v2/rate/USD/BRL")
    suspend fun getUsdToBrl(): ExchangeRateResponse
}