package com.payflow.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payflow.app.data.local.repository.AuthRepository
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference
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
        viewModelScope.launch {
            val user = repository.usuarioLogado()
            if (user != null) {
                _state.value = AuthUiState(usuario = user)
            }
        }
    }

    fun cadastrar(nome: String, email: String, senha: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, erro = null)
            repository.cadastrar(nome, email, senha)
                .onSuccess { user ->
                    _state.value = AuthUiState(usuario = user)
                }
                .onFailure { e ->
                    Log.e("AuthViewModel", "Cadastro falhou: ${e.message}")
                    _state.value = _state.value.copy(isLoading = false, erro = e.message)
                }
        }
    }

    fun loginComEmail(email: String, senha: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, erro = null)
            repository.loginComEmail(email, senha)
                .onSuccess { user ->
                    _state.value = AuthUiState(usuario = user)
                }
                .onFailure { e ->
                    Log.e("AuthViewModel", "Login falhou: ${e.message}")
                    _state.value = _state.value.copy(isLoading = false, erro = e.message)
                }
        }
    }

    fun loginComGoogle(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, erro = null)
            repository.loginComGoogle(context)
                .onSuccess { user ->
                    _state.value = AuthUiState(usuario = user)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isLoading = false, erro = e.message)
                }
        }
    }

    fun updateTheme(mode: AppThemeMode) {
        viewModelScope.launch {
            val user = _state.value.usuario ?: return@launch
            repository.updateTheme(user.id, mode)
            _state.value = _state.value.copy(usuario = user.copy(themeMode = mode))
        }
    }

    fun updateCurrency(currency: CurrencyPreference) {
        viewModelScope.launch {
            val user = _state.value.usuario ?: return@launch
            repository.updateCurrency(user.id, currency)
            _state.value = _state.value.copy(usuario = user.copy(currency = currency))
        }
    }

    fun mostrarCadastro() {
        _state.value = _state.value.copy(mostrarCadastro = true, erro = null)
    }

    fun fecharCadastro() {
        _state.value = _state.value.copy(mostrarCadastro = false, erro = null)
    }

    fun limparErro() {
        _state.value = _state.value.copy(erro = null)
    }

    fun logout() {
        repository.logout()
        _state.value = AuthUiState()
    }
}