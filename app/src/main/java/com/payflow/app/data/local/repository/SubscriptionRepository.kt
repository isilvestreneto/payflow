package com.payflow.app.data.local.repository

import com.payflow.app.data.local.dao.SubscriptionDao
import com.payflow.app.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

class SubscriptionRepository(private val subscriptionDao: SubscriptionDao) {

    suspend fun insertSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.insert(subscription)
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.update(subscription)
    }

    suspend fun getSubscriptionById(id: Int): SubscriptionEntity? {
        return subscriptionDao.getById(id)
    }

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAllSubscriptions()
    }
}
