package com.payflow.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase
import com.payflow.ui.screens.history.HistoryViewModel

class HistoryViewModelFactory(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(getSubscriptionsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}