package com.payflow.app.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.payflow.app.domain.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND senha = :senha LIMIT 1")
    suspend fun buscarPorEmailESenha(email: String, senha: String): User?
}