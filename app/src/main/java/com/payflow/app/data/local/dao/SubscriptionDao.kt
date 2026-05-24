package com.payflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.payflow.app.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity)

    @Update
    suspend fun update(subscription: SubscriptionEntity)

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: String): SubscriptionEntity?

    @Query("SELECT * FROM subscriptions ORDER BY dataCobrancaMillis ASC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE usuario_id = :userId ORDER BY dataCobrancaMillis ASC")
    fun getSubscriptionsByUserId(userId: String): Flow<List<SubscriptionEntity>>

    @Query("UPDATE subscriptions SET useCount = useCount + 1, dataAtualizacao = :timestamp WHERE id = :id")
    suspend fun incrementUseCount(id: String, timestamp: String)

    @Query("UPDATE subscriptions SET useCount = 0, lastResetDate = :resetDate WHERE usuario_id = :userId")
    suspend fun resetMonthlyUseCount(userId: String, resetDate: String)
}