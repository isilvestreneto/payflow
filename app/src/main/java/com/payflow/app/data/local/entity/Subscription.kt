package com.payflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class Subscription(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val valor: Double,
    val status: Boolean,
    val dataInicio: String,
    val dataFim: String,
    val formaPagamento: String,
    val categoria: String
)
