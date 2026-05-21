package com.payflow.app.viewmodel

import android.content.Context
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
        viewModelScope.launch {
            val user = repository.usuarioLogado()
            if (user != null) {
                _state.value = AuthUiState(usuario = user)
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
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.loginComEmail(email, senha)
                .onSuccess { user ->
                    _state.value = AuthUiState(usuario = user)
                }
                .onFailure { e ->
                    _state.value = AuthUiState(erro = e.message)
                }
        }
    }

    fun logout() {
        repository.logout()
        _state.value = AuthUiState()
    }


}