package com.payflow.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payflow.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        Log.e("AuthViewModel", "========================================")
        Log.e("AuthViewModel", "INIT - Criando usuário de teste")
        Log.e("AuthViewModel", "========================================")
        viewModelScope.launch {
            // Criar usuário de teste (dev only)
            repository.criarUsuarioTeste()
            
            val user = repository.usuarioLogado()
            if (user != null) {
                Log.e("AuthViewModel", "Usuário já logado: ${user.email}")
                _state.value = AuthUiState(usuario = user)
            } else {
                Log.e("AuthViewModel", "Nenhum usuário logado")
            }
        }
    }

    fun loginComGoogle(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.loginComGoogle(context)
                .onSuccess { user ->
                    _state.value = AuthUiState(usuario = user)
                }
                .onFailure { e ->
                    _state.value = AuthUiState(erro = e.message)
                }
        }
    }

    fun loginComEmail(email: String, senha: String) {
        Log.e("AuthViewModel", "========================================")
        Log.e("AuthViewModel", "LOGIN EMAIL: $email")
        Log.e("AuthViewModel", "========================================")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.loginComEmail(email, senha)
                .onSuccess { user ->
                    Log.e("AuthViewModel", "LOGIN SUCESSO: ${user.email}")
                    _state.value = AuthUiState(usuario = user)
                }
                .onFailure { e ->
                    Log.e("AuthViewModel", "LOGIN FALHOU: ${e.message}")
                    _state.value = AuthUiState(erro = e.message)
                }
        }
    }

    fun logout() {
        repository.logout()
        _state.value = AuthUiState()
    }


}