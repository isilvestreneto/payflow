package com.payflow.ui.screens.history

import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionStatus

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    object Empty : HistoryUiState()
    data class NoResults(
        val sortOption: SortOption = SortOption.DATE,
        val statusFilter: SubscriptionStatus? = null
    ) : HistoryUiState()

    data class Success(
        val subscriptions: List<Subscription>,
        val sortOption: SortOption = SortOption.DATE,
        val statusFilter: SubscriptionStatus? = null
    ) : HistoryUiState()

    data class Error(val message: String) : HistoryUiState()
}