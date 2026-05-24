package com.payflow.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase

class HomeViewModelFactory(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val userId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(getHomeSummaryUseCase, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}