package com.payflow.app.ui.screens.home

import com.payflow.app.domain.model.HomeSummary

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val summary: HomeSummary) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
