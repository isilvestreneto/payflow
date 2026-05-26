package com.payflow.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payflow.app.data.remote.repository.ExchangeRateRepository
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val userId: String,
    private val exchangeRateRepository: ExchangeRateRepository = ExchangeRateRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _exchangeRate = MutableStateFlow<Double?>(null)
    val exchangeRate: StateFlow<Double?> = _exchangeRate.asStateFlow()


    init {
        loadHomeSummary()
        loadExchangeRate()
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

    private fun loadExchangeRate() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Buscando cotação...")
            exchangeRateRepository.getUsdToBrl()
                .onSuccess { rate ->
                    Log.d("HomeViewModel", "Cotação recebida: $rate")
                    _exchangeRate.value = rate
                }
                .onFailure { e ->
                    Log.e("HomeViewModel", "Falha na cotação: ${e.message}")
                    _exchangeRate.value = null
                }
        }
    }

    fun onRetry() {
        loadHomeSummary()
        loadExchangeRate()
    }
}