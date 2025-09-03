package com.example.moneyflow.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.moneyflow.data.database.dao.AccountDao
import com.example.moneyflow.data.database.dao.CategoryDao
import com.example.moneyflow.data.database.dao.TransactionDao
import com.example.moneyflow.data.model.Account
import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.Transaction

/**
 * Room database for MoneyFlow application
 */
@Database(
    entities = [
        Account::class,
        Category::class,
        Transaction::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoneyFlowDatabase : RoomDatabase() {
    
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    
    companion object {
        const val DATABASE_NAME = "moneyflow_database"
        
        @Volatile
        private var INSTANCE: MoneyFlowDatabase? = null
        
        fun getDatabase(context: Context): MoneyFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoneyFlowDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // Remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
