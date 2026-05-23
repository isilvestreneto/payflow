package com.payflow.app.data.local.database

import androidx.room.TypeConverter
import com.payflow.app.domain.model.TipoLogin
import com.payflow.app.domain.model.SubscriptionStatus
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference

class Converters {
    @TypeConverter
    fun fromTipoLogin(value: TipoLogin): String = value.name

    @TypeConverter
    fun toTipoLogin(value: String): TipoLogin = TipoLogin.valueOf(value)

    @TypeConverter
    fun fromSubscriptionStatus(value: SubscriptionStatus): String = value.name

    @TypeConverter
    fun toSubscriptionStatus(value: String): SubscriptionStatus = SubscriptionStatus.valueOf(value)

    @TypeConverter
    fun fromAppThemeMode(value: AppThemeMode): String = value.name

    @TypeConverter
    fun toAppThemeMode(value: String): AppThemeMode = AppThemeMode.valueOf(value)

    @TypeConverter
    fun fromCurrencyPreference(value: CurrencyPreference): String = value.name

    @TypeConverter
    fun toCurrencyPreference(value: String): CurrencyPreference = CurrencyPreference.valueOf(value)
}