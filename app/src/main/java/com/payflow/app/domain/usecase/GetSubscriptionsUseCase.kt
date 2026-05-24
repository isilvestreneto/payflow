package com.payflow.app.domain.usecase

import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.domain.model.PaymentMethod
import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetSubscriptionsUseCase(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(userId: String): Flow<List<Subscription>> {
        return repository.getSubscriptionsByUserId(userId).map { entities ->
            entities.map { entity ->
                Subscription(
                    id = entity.id,
                    name = entity.nome,
                    value = entity.valorCentavos / 100.0,
                    status = entity.status,
                    type = SubscriptionType.entries.find { it.displayName == entity.categoria } ?: SubscriptionType.OUTROS,
                    paymentMethod = PaymentMethod.entries.find { it.displayName == entity.formaPagamento } ?: PaymentMethod.CREDIT_CARD,
                    billingDate = extractDayFromMillis(entity.dataCobrancaMillis),
                    useCount = 0,
                    createdAt = parseDate(entity.dataCriacao),
                    updatedAt = parseDate(entity.dataAtualizacao),
                    startDate = parseDate(entity.dataCriacao)
                )
            }
        }
    }

    private fun parseDate(dateStr: String): LocalDate {
        if (dateStr.isBlank()) return LocalDate.now()
        return try {
            LocalDate.parse(
                dateStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            )
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    private fun extractDayFromMillis(millis: Long): Int {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .dayOfMonth
    }
}