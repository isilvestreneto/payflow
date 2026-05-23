package com.payflow.app.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.payflow.app.domain.model.User
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND senha = :senha LIMIT 1")
    suspend fun buscarPorEmailESenha(email: String, senha: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun buscarPorId(userId: String): User?

    @Query("UPDATE users SET themeMode = :mode WHERE id = :userId")
    suspend fun updateTheme(userId: String, mode: AppThemeMode)

    @Query("UPDATE users SET currency = :currency WHERE id = :userId")
    suspend fun updateCurrency(userId: String, currency: CurrencyPreference)
}