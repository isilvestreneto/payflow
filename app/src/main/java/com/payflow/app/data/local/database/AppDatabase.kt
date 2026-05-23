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

@Database(entities = [SubscriptionEntity::class, User::class], version = 17, exportSchema = false)
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

        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `subscriptions` RENAME TO `subscriptions_old`")

                db.execSQL("""
                    CREATE TABLE `subscriptions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `nome` TEXT NOT NULL, 
                        `valorCentavos` INTEGER NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `dataCobrancaMillis` INTEGER NOT NULL, 
                        `formaPagamento` TEXT NOT NULL, 
                        `categoria` TEXT NOT NULL, 
                        `usuario_id` TEXT NOT NULL, 
                        `dataCriacao` TEXT NOT NULL, 
                        `dataAtualizacao` TEXT NOT NULL, 
                        FOREIGN KEY(`usuario_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO `subscriptions` (id, nome, valorCentavos, status, dataCobrancaMillis, formaPagamento, categoria, usuario_id, dataCriacao, dataAtualizacao)
                    SELECT id, nome, valorCentavos, 
                           CASE WHEN status = 1 THEN 'ACTIVE' ELSE 'PAUSED' END, 
                           dataCobrancaMillis, formaPagamento, categoria, usuario_id, dataCriacao, dataAtualizacao 
                    FROM `subscriptions_old`
                """)

                db.execSQL("DROP TABLE `subscriptions_old`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subscriptions_usuario_id` ON `subscriptions` (`usuario_id`)")
            }
        }

        private val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `subscriptions` RENAME TO `subscriptions_v13`")

                db.execSQL("""
                    CREATE TABLE `subscriptions` (
                        `id` TEXT PRIMARY KEY NOT NULL, 
                        `nome` TEXT NOT NULL, 
                        `valorCentavos` INTEGER NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `dataCobrancaMillis` INTEGER NOT NULL, 
                        `formaPagamento` TEXT NOT NULL, 
                        `categoria` TEXT NOT NULL, 
                        `usuario_id` TEXT NOT NULL, 
                        `dataCriacao` TEXT NOT NULL, 
                        `dataAtualizacao` TEXT NOT NULL, 
                        FOREIGN KEY(`usuario_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO `subscriptions` (id, nome, valorCentavos, status, dataCobrancaMillis, formaPagamento, categoria, usuario_id, dataCriacao, dataAtualizacao)
                    SELECT CAST(id AS TEXT), nome, valorCentavos, status, dataCobrancaMillis, formaPagamento, categoria, usuario_id, dataCriacao, dataAtualizacao 
                    FROM `subscriptions_v13`
                """)

                db.execSQL("DROP TABLE `subscriptions_v13`")
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
                    .addMigrations(MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}