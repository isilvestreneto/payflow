package com.payflow.app.data.remote

data class ExchangeRateResponse(
    val base: String,
    val quote: String,
    val rate: Double,
    val date: String
)