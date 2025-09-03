package com.example.moneyflow.di

import android.content.Context
import androidx.room.Room
import com.example.moneyflow.data.database.MoneyFlowDatabase
import com.example.moneyflow.data.database.dao.AccountDao
import com.example.moneyflow.data.database.dao.CategoryDao
import com.example.moneyflow.data.database.dao.TransactionDao
import com.example.moneyflow.domain.repository.AccountRepository
import com.example.moneyflow.data.repository.AccountRepositoryImpl
import com.example.moneyflow.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing database and repository dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMoneyFlowDatabase(@ApplicationContext context: Context): MoneyFlowDatabase {
        return Room.databaseBuilder(
            context,
            MoneyFlowDatabase::class.java,
            MoneyFlowDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Remove this in production
            .build()
    }

    @Provides
    fun provideAccountDao(database: MoneyFlowDatabase): AccountDao {
        return database.accountDao()
    }

    @Provides
    fun provideCategoryDao(database: MoneyFlowDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideTransactionDao(database: MoneyFlowDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideAccountRepository(accountDao: AccountDao): AccountRepository {
        return AccountRepositoryImpl(accountDao)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        accountDao: AccountDao,
        categoryDao: CategoryDao
    ): TransactionRepository {
        return TransactionRepository(transactionDao, accountDao, categoryDao)
    }
}
