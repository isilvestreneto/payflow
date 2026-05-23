package com.payflow.app.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDate
import java.util.Date

data class Subscription @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: Long = 0,
    val name: String,
    val value: Double,
    val status: SubscriptionStatus,
    val type: SubscriptionType,
    val paymentMethod: PaymentMethod,
    val startDate: LocalDate,
    val billingDate: Int, // Dia do mês (1-31)
    val lastUseDate: Date? = null,
    val useCount: Int = 0, // Quantas vezes usou no mês
    val notes: String? = null,
    val iconResId: Int? = null, // ID do recurso drawable do ícone
    val iconUri: String? = null, // URI para ícone customizado/upload
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDate = LocalDate.now()
)
