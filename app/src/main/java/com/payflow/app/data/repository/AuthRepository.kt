package com.payflow.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.payflow.app.BuildConfig
import com.payflow.app.domain.model.TipoLogin
import com.payflow.app.domain.model.User
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class AuthRepository(
    private val userDao: UserDao,
    private val credentialManager: CredentialManager,
    private val preferencias: SharedPreferences
) {
    // Login com email/senha (valida hash no Room)
    suspend fun loginComEmail(email: String, senha: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val senhaHash = hashSenha(senha)
                val user = userDao.buscarPorEmailESenha(email, senhaHash)
                if (user != null) {
                    salvarSessao(user.id)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Email ou senha incorretos"))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Erro login email: ${e.message}", e)
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
                        .setServerClientId(BuildConfig.WEB_CLIENT_ID).build()

                val request =
                    GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

                val result = credentialManager.getCredential(
                    request = request, context = context
                )

                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleCred = GoogleIdTokenCredential.createFrom(credential.data)

                            val user = User(
                                id = googleCred.id,
                                nome = googleCred.displayName ?: "",
                                email = googleCred.id,
                                tipoLogin = TipoLogin.GOOGLE,
                                senha = null,
                                fotoUrl = googleCred.profilePictureUri?.toString()
                            )
                            userDao.inserir(user)
                            salvarSessao(user.id)

                            Result.success(user)
                        } else {
                            Result.failure(Exception("Tipo de credencial inválido"))
                        }
                    }

                    else -> Result.failure(Exception("Credencial não reconhecida"))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Erro login Google: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    // Cadastro local (email/senha) — salva hash, persiste sessão
    suspend fun cadastrar(nome: String, email: String, senha: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // Verifica se email já existe
                val existente = userDao.buscarPorId(email)
                if (existente != null) {
                    return@withContext Result.failure(Exception("Email já cadastrado"))
                }

                val user = User(
                    id = email,
                    nome = nome,
                    email = email,
                    tipoLogin = TipoLogin.EMAIL,
                    senha = hashSenha(senha)
                )
                userDao.inserir(user)
                salvarSessao(user.id)
                Result.success(user)
            } catch (e: Exception) {
                Log.e("AuthRepository", "Erro cadastro: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun usuarioLogado(): User? {
        val userId = preferencias.getString("userId", null) ?: return null
        return withContext(Dispatchers.IO) { userDao.buscarPorId(userId) }
    }

    private fun salvarSessao(userId: String) {
        preferencias.edit().putString("userId", userId).apply()
    }

    fun logout() {
        preferencias.edit().remove("userId").apply()
    }

    // SHA-256 — não é bcrypt, mas é o mínimo aceitável pra não salvar texto puro
    private fun hashSenha(senha: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(senha.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Criar usuário de teste para desenvolvimento
    suspend fun criarUsuarioTeste() {
        withContext(Dispatchers.IO) {
            try {
                val existente = userDao.buscarPorId("teste@payflow.com")
                if (existente != null) return@withContext

                val user = User(
                    id = "teste@payflow.com",
                    nome = "Usuário Teste",
                    email = "teste@payflow.com",
                    tipoLogin = TipoLogin.EMAIL,
                    senha = hashSenha("123456")
                )
                userDao.inserir(user)
                Log.d("AuthRepository", "Usuário de teste criado: teste@payflow.com / 123456")
            } catch (e: Exception) {
                Log.e("AuthRepository", "Erro ao criar usuário de teste: ${e.message}", e)
            }
        }
    }

    suspend fun updateTheme(userId: String, mode: AppThemeMode) {
        withContext(Dispatchers.IO) {
            userDao.updateTheme(userId, mode)
        }
    }

    suspend fun updateCurrency(userId: String, currency: CurrencyPreference) {
        withContext(Dispatchers.IO) {
            userDao.updateCurrency(userId, currency)
        }
    }
}