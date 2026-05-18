package com.payflow.app.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.payflow.app.domain.model.TipoLogin
import com.payflow.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val userDao: UserDao, private val credentialManager: CredentialManager
) {
    // Login com email/senha (fake, valida no Room)
    suspend fun loginComEmail(email: String, senha: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.buscarPorEmailESenha(email, senha)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Email ou senha incorretos"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Login com Google (OAuth via Credential Manager)
    suspend fun loginComGoogle(context: Context): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val googleIdOption =
                    GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
                        .setServerClientId(WEB_CLIENT_ID).build()

                val request =
                    GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

                val result = credentialManager.getCredential(
                    request = request, context = context
                )

                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleCred = GoogleIdTokenCredential.createFrom(credential.data)

                            // Cria ou atualiza user no banco local
                            val user = User(
                                id = googleCred.id,
                                nome = googleCred.displayName ?: "",
                                email = googleCred.id,
                                tipoLogin = TipoLogin.GOOGLE,
                                senha = ""
                            )
                            userDao.inserir(user)

                            Result.success(user)
                        } else {
                            Result.failure(Exception("Tipo de credencial inválido"))
                        }
                    }

                    else -> Result.failure(Exception("Credencial não reconhecida"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
