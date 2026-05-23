package com.payflow.app.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.payflow.app.data.local.dao.SubscriptionDao
import com.payflow.app.data.local.entity.SubscriptionEntity
import com.payflow.app.domain.model.User

@Database(
    entities = [User::class, SubscriptionEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context) = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context, AppDatabase::class.java, "app.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}