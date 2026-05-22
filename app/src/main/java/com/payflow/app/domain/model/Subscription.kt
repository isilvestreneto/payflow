package com.payflow.app.domain.model

import java.util.Date

data class Subscription(
    val id: Long = 0,
    val name: String,
    val value: Double,
    val status: SubscriptionStatus,
    val type: SubscriptionType,
    val paymentMethod: PaymentMethod,
    val startDate: Date,
    val billingDate: Int, // Dia do mês (1-31)
    val lastUseDate: Date? = null,
    val useCount: Int = 0, // Quantas vezes usou no mês
    val notes: String? = null,
    val iconResId: Int? = null, // ID do recurso drawable do ícone
    val iconUri: String? = null, // URI para ícone customizado/upload
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
