package com.example.moneyflow.di

import com.example.moneyflow.data.database.dao.CategoryDao
import com.example.moneyflow.data.repository.CategoryRepositoryImpl
import com.example.moneyflow.domain.repository.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for Category-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {
    
    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }
}
