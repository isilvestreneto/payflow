package com.payflow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.payflow.app.domain.model.User

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["usuario_id"])]
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val valorCentavos: Long, // Armazenado como centavos para evitar erros de precisão
    val status: Boolean,
    val dataCobrancaMillis: Long, // Armazenado como Long para facilitar ordenação e consultas
    val formaPagamento: String,
    val categoria: String,

    @ColumnInfo(name = "usuario_id")
    val usuarioId: String,

    val dataCriacao: String,
    val dataAtualizacao: String
)
