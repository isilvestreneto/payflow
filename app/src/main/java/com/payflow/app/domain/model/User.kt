package com.payflow.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,  // email ou UID do Google
    val nome: String,
    val email: String,
    val tipoLogin: TipoLogin,  // EMAIL ou GOOGLE
    val senha: String?,        // null quando tipoLogin = GOOGLE
)