package com.payflow.app.domain.usecase

import com.payflow.app.domain.model.HomeSummary
import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetHomeSummaryUseCase(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) {
    operator fun invoke(userId: String): Flow<HomeSummary> {
        return getSubscriptionsUseCase(userId).map { subscriptions ->
            calculateSummary(subscriptions)
        }
    }

    private fun calculateSummary(subscriptions: List<Subscription>): HomeSummary {
        val activeSubscriptions = subscriptions.filter { it.status == SubscriptionStatus.ACTIVE }

        val activeCount = activeSubscriptions.size
        val monthlySpending = activeSubscriptions.sumOf { it.value }
        val averageValue = if (activeCount > 0) monthlySpending / activeCount else 0.0

        val mostExpensive = activeSubscriptions.maxByOrNull { it.value }
        val cheapest = activeSubscriptions.minByOrNull { it.value }
        val mostUsed = activeSubscriptions.maxByOrNull { it.useCount }

        return HomeSummary(
            activeSubscriptionsCount = activeCount,
            monthlySpending = monthlySpending,
            averageValue = averageValue,
            mostExpensive = mostExpensive,
            cheapest = cheapest,
            mostUsed = mostUsed
        )
    }
}