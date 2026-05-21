package com.payflow.app.domain.usecase

import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.domain.model.PaymentMethod
import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionStatus
import com.payflow.app.domain.model.SubscriptionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class GetSubscriptionsUseCase(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<List<Subscription>> {
        return repository.getAllSubscriptions().map { entities ->
            entities.map { entity ->
                Subscription(
                    id = entity.id.toLong(),
                    name = entity.nome,
                    value = entity.valorCentavos / 100.0,
                    status = if (entity.status) SubscriptionStatus.ACTIVE else SubscriptionStatus.CANCELED,
                    type = SubscriptionType.entries.find { it.displayName == entity.categoria } ?: SubscriptionType.OUTROS,
                    paymentMethod = PaymentMethod.entries.find { it.displayName == entity.formaPagamento } ?: PaymentMethod.CREDIT_CARD,
                    startDate = Date(entity.dataCobrancaMillis),
                    billingDate = extractDayFromMillis(entity.dataCobrancaMillis),
                    useCount = 0,
                    createdAt = parseDate(entity.dataCriacao),
                    updatedAt = parseDate(entity.dataAtualizacao)
                )
            }
        }
    }

    private fun parseDate(dateStr: String): Date {
        if (dateStr.isBlank()) return Date()
        return try {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    private fun extractDayFromMillis(millis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
}
