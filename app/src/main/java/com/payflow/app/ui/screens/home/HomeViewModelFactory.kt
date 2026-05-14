package com.payflow.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase

class HomeViewModelFactory(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(getHomeSummaryUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
