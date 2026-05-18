package com.payflow.app.domain.usecase

import android.content.res.Resources
import com.payflow.app.domain.model.PaymentMethod
import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionStatus
import com.payflow.app.domain.model.SubscriptionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date

// UseCase temporário com dados mockados para desenvolvimento
// Será substituído por chamada ao Repository quando implementado
class GetSubscriptionsUseCase {
    operator fun invoke(): Flow<List<Subscription>> {
        return flowOf(getMockSubscriptions())
    }

    private fun getMockSubscriptions(): List<Subscription> {
        val now = Date()
        
        return listOf(
            Subscription(
                id = 1,
                name = "Netflix",
                value = 45.90,
                status = SubscriptionStatus.ACTIVE,
                type = SubscriptionType.STREAMING,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                startDate = now,
                billingDate = 15,
                useCount = 28,
                iconResId = android.R.drawable.ic_dialog_info, // Ícone temporário
                createdAt = now
            ),
            Subscription(
                id = 2,
                name = "Spotify Premium",
                value = 21.90,
                status = SubscriptionStatus.ACTIVE,
                type = SubscriptionType.STREAMING,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                startDate = now,
                billingDate = 10,
                useCount = 45,
                iconResId = android.R.drawable.ic_dialog_info,
                createdAt = now
            ),
            Subscription(
                id = 3,
                name = "ChatGPT Plus",
                value = 20.00,
                status = SubscriptionStatus.ACTIVE,
                type = SubscriptionType.IA,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                startDate = now,
                billingDate = 5,
                useCount = 35,
                iconResId = android.R.drawable.ic_dialog_info,
                createdAt = now
            ),
            Subscription(
                id = 4,
                name = "Amazon Prime",
                value = 19.90,
                status = SubscriptionStatus.ACTIVE,
                type = SubscriptionType.SERVICOS,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                startDate = now,
                billingDate = 20,
                useCount = 12,
                iconResId = android.R.drawable.ic_dialog_info,
                createdAt = now
            ),
            Subscription(
                id = 5,
                name = "Disney+",
                value = 43.90,
                status = SubscriptionStatus.ACTIVE,
                type = SubscriptionType.STREAMING,
                paymentMethod = PaymentMethod.PIX,
                startDate = now,
                billingDate = 25,
                useCount = 8,
                iconResId = android.R.drawable.ic_dialog_info,
                createdAt = now
            )
        )
    }
}
