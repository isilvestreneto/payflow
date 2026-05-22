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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val userDao: UserDao,
    private val credentialManager: CredentialManager,
    private val preferencias: SharedPreferences
) {
    // Login com email/senha (fake, valida no Room)
    suspend fun loginComEmail(email: String, senha: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                Log.e("AuthRepository", "Buscando usuário: $email / $senha")
                val user = userDao.buscarPorEmailESenha(email, senha)
                if (user != null) {
                    Log.e("AuthRepository", "Usuário encontrado: ${user.nome}")
                    Result.success(user)
                } else {
                    Log.e("AuthRepository", "Usuário NÃO encontrado")
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
                Log.e("AuthRepository", "Erro login Google: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    // Cadastro local (email/senha)
    suspend fun cadastrar(nome: String, email: String, senha: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val user = User(
                    id = email,
                    nome = nome,
                    email = email,
                    tipoLogin = TipoLogin.EMAIL,
                    senha = senha
                )
                userDao.inserir(user)
                Result.success(user)
            } catch (e: Exception) {
                Log.e("AuthRepository", "Erro cadastro: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun usuarioLogado(): User? {
        val userId = preferencias.getString("userId", null) ?: return null
        return userDao.buscarPorId(userId)
    }

    // nos logins bem-sucedidos, salva o ID:
    private fun salvarSessao(userId: String) {
        preferencias.edit().putString("userId", userId).apply()
    }

    fun logout() {
        preferencias.edit().remove("userId").apply()
    }

    // Criar usuário de teste para desenvolvimento
    suspend fun criarUsuarioTeste() {
        withContext(Dispatchers.IO) {
            try {
                Log.e("AuthRepository", "========================================")
                Log.e("AuthRepository", "CRIANDO USUÁRIO DE TESTE")
                // Verificar se já existe
                val existente = userDao.buscarPorId("teste@payflow.com")
                if (existente != null) {
                    Log.e("AuthRepository", "✓ Usuário de teste JÁ EXISTE: teste@payflow.com / 123456")
                    Log.e("AuthRepository", "========================================")
                    return@withContext
                }
                
                val user = User(
                    id = "teste@payflow.com",
                    nome = "Usuário Teste",
                    email = "teste@payflow.com",
                    tipoLogin = TipoLogin.EMAIL,
                    senha = "123456"
                )
                userDao.inserir(user)
                Log.e("AuthRepository", "✓ Usuário de teste CRIADO COM SUCESSO: teste@payflow.com / 123456")
                Log.e("AuthRepository", "========================================")
            } catch (e: Exception) {
                Log.e("AuthRepository", "✗ ERRO FATAL ao criar usuário de teste: ${e.message}", e)
                Log.e("AuthRepository", "========================================")
            }
        }
    }
}
