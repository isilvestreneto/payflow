package com.payflow.app.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.payflow.app.data.local.dao.SubscriptionDao

abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao


    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                        CREATE TABLE IF NOT EXISTS `Subscription` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `nome` TEXT NOT NULL,
                            `valor` REAL NOT NULL,
                            `status` INTEGER NOT NULL,
                            `dataInicio` TEXT NOT NULL,
                            `dataFim` TEXT NOT NULL,
                            `formaPagamento` TEXT NOT NULL,
                            `categoria` TEXT NOT NULL
                        )
                    """
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

}