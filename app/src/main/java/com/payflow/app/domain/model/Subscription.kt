package com.payflow.app.domain.model

import java.time.LocalDate

data class Subscription(
    val id: String,
    val name: String,
    val value: Double,
    val status: SubscriptionStatus,
    val type: SubscriptionType,
    val paymentMethod: PaymentMethod,
    val billingDate: Int, // Dia do mês (1-31)
    val lastUseDate: LocalDate? = null,
    val useCount: Int = 0, // Quantas vezes usou no mês
    val notes: String? = null,
    val iconResId: Int? = null, // ID do recurso drawable do ícone
    val iconUri: String? = null, // URI para ícone customizado/upload
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDate = LocalDate.now(),
    val startDate: LocalDate
)
