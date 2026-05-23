package com.payflow.app.data.local.database

import androidx.room.TypeConverter
import com.payflow.app.domain.model.TipoLogin

class Converters {
    @TypeConverter
    fun fromTipoLogin(value: TipoLogin): String {
        return value.name
    }

    @TypeConverter
    fun toTipoLogin(value: String): TipoLogin {
        return TipoLogin.valueOf(value)
    }
}
