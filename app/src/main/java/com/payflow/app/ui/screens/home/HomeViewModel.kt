package com.payflow.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeSummary()
    }

    fun loadHomeSummary() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            getHomeSummaryUseCase(userId)
                .catch { exception ->
                    _uiState.value = HomeUiState.Error(
                        exception.message ?: "Erro ao carregar dados"
                    )
                }
                .collect { summary ->
                    _uiState.value = HomeUiState.Success(summary)
                }
        }
    }

    fun onRetry() {
        loadHomeSummary()
    }
}