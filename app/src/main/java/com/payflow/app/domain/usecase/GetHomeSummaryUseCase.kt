package com.payflow.app.domain.usecase

import com.payflow.app.domain.model.HomeSummary
import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetHomeSummaryUseCase(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) {
    operator fun invoke(userId: String): Flow<HomeSummary> {
        return getSubscriptionsUseCase(userId).map { subscriptions ->
            calculateSummary(subscriptions)
        }
    }

    private fun calculateSummary(subscriptions: List<Subscription>): HomeSummary {
        val active = subscriptions.filter { it.status == SubscriptionStatus.ACTIVE }
        val activeCount = active.size
        val monthlySpending = active.sumOf { it.value }
        val averageValue = if (activeCount > 0) monthlySpending / activeCount else 0.0

        // Pouco usadas = ativas com menos de 2 usos no mês, ordenadas por uso crescente
        val leastUsed = active
            .filter { it.useCount < 2 }
            .sortedBy { it.useCount }
            .take(3)

        // Próxima a vencer = assinatura ativa cujo dia de cobrança está mais próximo (a partir de hoje)
        val today = LocalDate.now()
        val currentDay = today.dayOfMonth
        val nextDue = active
            .map { subscription ->
                val billingDay = subscription.billingDate
                val daysUntilDue = if (billingDay >= currentDay) {
                    billingDay - currentDay
                } else {
                    // Próximo mês
                    val daysInMonth = today.lengthOfMonth()
                    (daysInMonth - currentDay) + billingDay
                }
                subscription to daysUntilDue
            }
            .minByOrNull { it.second }
            ?.first

        return HomeSummary(
            activeSubscriptionsCount = activeCount,
            monthlySpending = monthlySpending,
            averageValue = averageValue,
            mostExpensive = active.maxByOrNull { it.value },
            cheapest = active.minByOrNull { it.value },
            mostUsed = active.maxByOrNull { it.useCount },
            leastUsed = leastUsed,
            nextDue = nextDue
        )
    }
}