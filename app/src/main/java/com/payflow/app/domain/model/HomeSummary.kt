package com.payflow.app.domain.model

data class HomeSummary(
    val activeSubscriptionsCount: Int,
    val monthlySpending: Double,
    val averageValue: Double,
    val mostExpensive: Subscription?,
    val cheapest: Subscription?,
    val mostUsed: Subscription?
)
