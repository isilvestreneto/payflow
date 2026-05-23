package com.payflow.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionStatus
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.sortedBy
import kotlin.collections.sortedByDescending

class HistoryViewModel(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var allSubscriptions: List<Subscription> = emptyList()
    private var currentSortOption = SortOption.DATE
    private var currentStatusFilter: SubscriptionStatus? = null
    private var currentStartDate: LocalDate? = null
    private var currentEndDate: LocalDate? = null

    init {
        loadSubscriptions()
    }

    private fun loadSubscriptions() {
        viewModelScope.launch {
            try {
                _uiState.value = HistoryUiState.Loading

                getSubscriptionsUseCase().collect { subscriptions ->
                    allSubscriptions = subscriptions

                    if (subscriptions.isEmpty()) {
                        _uiState.value = HistoryUiState.Empty
                    } else {
                        applyFiltersAndSort()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun changeSortOption(option: SortOption) {
        currentSortOption = option
        applyFiltersAndSort()
    }

    fun updateStatusFilter(status: SubscriptionStatus?) {
        currentStatusFilter = status
        applyFiltersAndSort()
    }

    fun updatePeriodFilter(startDate: LocalDate?, endDate: LocalDate?) {
        currentStartDate = startDate
        currentEndDate = endDate
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        var filtered = allSubscriptions

        // Aplicar filtro de status
        if (currentStatusFilter != null) {
            filtered = filtered.filter { it.status == currentStatusFilter }
        }

        // Aplicar filtro de período
        if (currentStartDate != null || currentEndDate != null) {
            filtered = filtered.filter { subscription ->
                val inRange = when {
                    currentStartDate != null && currentEndDate != null -> {
                        subscription.startDate.isAfter(currentStartDate) &&
                                subscription.startDate.isBefore(currentEndDate)
                    }
                    currentStartDate != null -> {
                        subscription.startDate.isAfter(currentStartDate)
                    }
                    currentEndDate != null -> {
                        subscription.startDate.isBefore(currentEndDate)
                    }
                    else -> true
                }
                inRange
            }
        }

        // Aplicar ordenação
        val sorted = when (currentSortOption) {
            SortOption.DATE -> filtered.sortedByDescending { it.startDate }
            SortOption.NAME -> filtered.sortedBy { it.name }
            SortOption.STATUS -> filtered.sortedBy { it.status.ordinal }
            SortOption.VALUE -> filtered.sortedByDescending { it.value }
        }

        if (sorted.isEmpty()) {
            _uiState.value = HistoryUiState.NoResults(
                sortOption = currentSortOption,
                statusFilter = currentStatusFilter
            )
        } else {
            _uiState.value = HistoryUiState.Success(
                subscriptions = sorted,
                sortOption = currentSortOption,
                statusFilter = currentStatusFilter
            )
        }
    }
}

enum class SortOption(val label: String) {
    DATE("Data"),
    NAME("Nome"),
    STATUS("Status"),
    VALUE("Valor")
}