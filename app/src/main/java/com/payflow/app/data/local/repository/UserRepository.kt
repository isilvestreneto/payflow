package com.payflow.app.data.local.repository

import com.payflow.app.data.repository.UserDao
import com.payflow.app.domain.model.TipoLogin
import com.payflow.app.domain.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun ensureDefaultUser() {
        val defaultUser = User(
            id = "user_default",
            nome = "Usuário Padrão",
            email = "default@payflow.com",
            tipoLogin = TipoLogin.EMAIL,
            senha = "123"
        )
        userDao.inserir(defaultUser)
    }
}
