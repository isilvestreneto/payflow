package com.payflow.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.payflow.app.data.local.dao.SubscriptionDao
import com.payflow.app.data.repository.UserDao
import com.payflow.app.data.local.entity.SubscriptionEntity
import com.payflow.app.domain.model.User

@Database(entities = [SubscriptionEntity::class, User::class], version = 12, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `nome` TEXT NOT NULL, `email` TEXT NOT NULL, `tipoLogin` TEXT NOT NULL, `senha` TEXT, PRIMARY KEY(`id`))")
                
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `subscriptions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `nome` TEXT NOT NULL, 
                        `valorCentavos` INTEGER NOT NULL, 
                        `status` INTEGER NOT NULL, 
                        `dataCobrancaMillis` INTEGER NOT NULL, 
                        `formaPagamento` TEXT NOT NULL, 
                        `categoria` TEXT NOT NULL, 
                        `usuario_id` TEXT NOT NULL, 
                        `dataCriacao` TEXT NOT NULL, 
                        `dataAtualizacao` TEXT NOT NULL, 
                        FOREIGN KEY(`usuario_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subscriptions_usuario_id` ON `subscriptions` (`usuario_id`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_11_12)
                .fallbackToDestructiveMigrationOnDowngrade() // Mantém segurança em produção
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
