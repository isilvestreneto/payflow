package com.payflow.app.data.local.repository

import com.payflow.app.data.local.dao.SubscriptionDao
import com.payflow.app.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubscriptionRepository(private val subscriptionDao: SubscriptionDao) {

    suspend fun insertSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.insert(subscription)
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.update(subscription)
    }

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAllSubscriptions()
    }

    fun getSubscriptionsByUserId(userId: String): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getSubscriptionsByUserId(userId)
    }

    suspend fun incrementUseCount(id: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        subscriptionDao.incrementUseCount(id, timestamp)
    }

    suspend fun resetMonthlyUseCountIfNeeded(userId: String) {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val entity = subscriptionDao.getSubscriptionsByUserId(userId)
        // Reset é chamado pelo ViewModel na abertura do app — lógica de verificação lá
        val resetDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        subscriptionDao.resetMonthlyUseCount(userId, resetDate)
    }

    suspend fun resetMonthlyUseCount(userId: String) {
        val resetDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        subscriptionDao.resetMonthlyUseCount(userId, resetDate)
    }
}