package com.example.moneyflow.data.repository

import com.example.moneyflow.data.database.dao.CategoryDao
import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.CategoryType
import com.example.moneyflow.data.model.CategorySummary
import com.example.moneyflow.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CategoryRepository
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    
    override fun getAllActiveCategories(): Flow<List<Category>> {
        return categoryDao.getAllActiveCategories()
    }
    
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }
    
    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }
    
    override suspend fun getCategoryById(categoryId: Long): Category? {
        return try {
            categoryDao.getCategoryById(categoryId)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getCategoryByName(name: String): Category? {
        return try {
            categoryDao.getCategoryByName(name)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getCategoriesByTypeSync(type: CategoryType): List<Category> {
        return try {
            categoryDao.getCategoriesByTypeSync(type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override fun getDefaultCategories(): Flow<List<Category>> {
        return categoryDao.getDefaultCategories()
    }
    
    override fun searchCategories(searchQuery: String): Flow<List<Category>> {
        return categoryDao.searchCategories(searchQuery)
    }
    
    override suspend fun getCategorySummary(): CategorySummary {
        return try {
            val totalCategories = categoryDao.getTotalCategoriesCount()
            val expenseCount = categoryDao.getCategoryCountByType(CategoryType.EXPENSE)
            val incomeCount = categoryDao.getCategoryCountByType(CategoryType.INCOME)
            val savingsCount = categoryDao.getCategoryCountByType(CategoryType.SAVINGS)
            
            val expenseCategories = categoryDao.getCategoriesByTypeSync(CategoryType.EXPENSE)
            val incomeCategories = categoryDao.getCategoriesByTypeSync(CategoryType.INCOME)
            val savingsCategories = categoryDao.getCategoriesByTypeSync(CategoryType.SAVINGS)
            
            CategorySummary(
                totalCategories = totalCategories,
                activeCategories = totalCategories,
                categoriesByType = mapOf(
                    CategoryType.EXPENSE to expenseCount,
                    CategoryType.INCOME to incomeCount,
                    CategoryType.SAVINGS to savingsCount
                ),
                expenseCategories = expenseCategories,
                incomeCategories = incomeCategories,
                savingsCategories = savingsCategories
            )
        } catch (e: Exception) {
            CategorySummary(
                totalCategories = 0,
                activeCategories = 0,
                categoriesByType = emptyMap(),
                expenseCategories = emptyList(),
                incomeCategories = emptyList(),
                savingsCategories = emptyList()
            )
        }
    }
    
    override suspend fun createCategory(category: Category): Result<Long> {
        return try {
            // Check if category name already exists
            if (isCategoryNameExists(category.name)) {
                return Result.failure(Exception("Category with this name already exists"))
            }
            
            val categoryId = categoryDao.insertCategory(category)
            Result.success(categoryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCategory(category: Category): Result<Unit> {
        return try {
            // Check if category name already exists (excluding current category)
            if (isCategoryNameExists(category.name, category.id)) {
                return Result.failure(Exception("Category with this name already exists"))
            }
            
            val updatedCategory = category.copy(updatedAt = Date())
            categoryDao.updateCategory(updatedCategory)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCategory(categoryId: Long): Result<Unit> {
        return try {
            categoryDao.softDeleteCategory(categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCategoryById(categoryId: Long): Result<Unit> {
        return try {
            categoryDao.deleteCategoryById(categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun isCategoryNameExists(name: String, excludeId: Long): Boolean {
        return try {
            categoryDao.isCategoryNameExists(name, excludeId)
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun initializeDefaultCategories(): Result<Unit> {
        return try {
            // Check if default categories already exist
            val existingCategories = categoryDao.getTotalCategoriesCount()
            if (existingCategories > 0) {
                return Result.success(Unit)
            }
            
            val defaultCategories = getDefaultCategoriesList()
            categoryDao.insertCategories(defaultCategories)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getDefaultCategoriesList(): List<Category> {
        val currentDate = Date()
        
        return listOf(
            // Income Categories
            Category(
                name = "Salary",
                type = CategoryType.INCOME,
                iconName = "money",
                color = "#4CAF50",
                description = "Monthly salary and wages",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Freelance",
                type = CategoryType.INCOME,
                iconName = "budget",
                color = "#2196F3",
                description = "Freelance income",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Business",
                type = CategoryType.INCOME,
                iconName = "analysis",
                color = "#FF9800",
                description = "Business income",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Investment",
                type = CategoryType.INCOME,
                iconName = "category",
                color = "#9C27B0",
                description = "Investment returns",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            
            // Expense Categories
            Category(
                name = "Food & Dining",
                type = CategoryType.EXPENSE,
                iconName = "category",
                color = "#F44336",
                description = "Restaurant, groceries, food delivery",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Transportation",
                type = CategoryType.EXPENSE,
                iconName = "home",
                color = "#673AB7",
                description = "Gas, public transport, taxi",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Shopping",
                type = CategoryType.EXPENSE,
                iconName = "card",
                color = "#E91E63",
                description = "Clothing, electronics, general shopping",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Bills & Utilities",
                type = CategoryType.EXPENSE,
                iconName = "account",
                color = "#607D8B",
                description = "Electricity, water, internet, phone",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Healthcare",
                type = CategoryType.EXPENSE,
                iconName = "lightbulb",
                color = "#009688",
                description = "Doctor visits, medicine, insurance",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Entertainment",
                type = CategoryType.EXPENSE,
                iconName = "social",
                color = "#FF5722",
                description = "Movies, games, subscriptions",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Education",
                type = CategoryType.EXPENSE,
                iconName = "budget",
                color = "#795548",
                description = "Books, courses, tuition",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            
            // Savings Categories
            Category(
                name = "Emergency Fund",
                type = CategoryType.SAVINGS,
                iconName = "savings",
                color = "#4CAF50",
                description = "Emergency savings",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Vacation",
                type = CategoryType.SAVINGS,
                iconName = "wallet",
                color = "#2196F3",
                description = "Vacation and travel savings",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Category(
                name = "Retirement",
                type = CategoryType.SAVINGS,
                iconName = "wallet_account",
                color = "#FF9800",
                description = "Retirement savings",
                isDefault = true,
                createdAt = currentDate,
                updatedAt = currentDate
            )
        )
    }
}
