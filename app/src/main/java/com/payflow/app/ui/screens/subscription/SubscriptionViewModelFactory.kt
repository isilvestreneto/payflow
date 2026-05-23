package com.payflow.app.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.data.local.repository.UserRepository

class SubscriptionViewModelFactory(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubscriptionViewModel(subscriptionRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
