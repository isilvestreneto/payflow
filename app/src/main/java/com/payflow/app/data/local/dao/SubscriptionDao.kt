package com.payflow.app.data.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.payflow.app.data.local.entity.Subscription

interface SubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: Subscription)
}