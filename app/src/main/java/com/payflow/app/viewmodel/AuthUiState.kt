package com.payflow.app.viewmodel

import com.payflow.app.domain.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val erro: String? = null,
    val usuario: User? = null
)