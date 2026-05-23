package com.payflow.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val nome: String,
    val email: String,
    val tipoLogin: TipoLogin,
    val senha: String?,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val currency: CurrencyPreference = CurrencyPreference.BRL,
    val fotoUrl: String? = null
)
