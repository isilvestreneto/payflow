package com.payflow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.payflow.app.domain.model.User
import com.payflow.app.domain.model.SubscriptionStatus
import java.util.UUID

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
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val valorCentavos: Long,
    val status: SubscriptionStatus,
    val dataCobrancaMillis: Long,
    val formaPagamento: String,
    val categoria: String,

    @ColumnInfo(name = "usuario_id")
    val usuarioId: String,

    val dataCriacao: String,
    val dataAtualizacao: String
)
