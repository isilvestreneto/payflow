package com.payflow.app.domain.model

enum class PaymentMethod(val displayName: String) {
    CREDIT_CARD("Cartão de Crédito"),
    PIX("PIX"),
    BOLETO("Boleto"),
    DEBIT("Débito")
}
