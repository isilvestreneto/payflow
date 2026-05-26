package com.payflow.app.data.remote.repository

import android.util.Log
import com.payflow.app.data.remote.ExchangeRateService
import com.payflow.app.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExchangeRateRepository(
    private val service: ExchangeRateService = RetrofitClient.exchangeRateService
) {
    suspend fun getUsdToBrl(): Result<Double> {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getUsdToBrl()
                Result.success(response.rate)
            } catch (e: Exception) {
                Log.e("ExchangeRateRepo", "Erro ao buscar cotação: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
}