package com.payflow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class Subscription(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val valor: Double,
    val status: Boolean,
    val dataInicio: String,
    val dataFim: String,
    val formaPagamento: String,
    val categoria: String,

    @ColumnInfo(name = "usuario_id")
    val usuarioId: String,

    val dataCriacao: String,
    val dataAtualizacao: String,
)
