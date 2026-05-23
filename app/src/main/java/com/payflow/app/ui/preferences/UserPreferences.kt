package com.payflow.app.ui.preferences

enum class AppThemeMode(val label: String) {
    LIGHT("Claro"),
    DARK("Escuro"),
    SYSTEM("Sistema")
}

enum class CurrencyPreference(val code: String, val label: String) {
    BRL(code = "BRL", label = "Real brasileiro"),
    USD(code = "USD", label = "Dolar americano")
}
