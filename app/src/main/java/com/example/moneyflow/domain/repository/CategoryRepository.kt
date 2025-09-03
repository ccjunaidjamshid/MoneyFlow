package com.example.moneyflow.domain.repository

import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.CategoryType
import com.example.moneyflow.data.model.CategorySummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Category operations
 */
interface CategoryRepository {
    
    /**
     * Get all active categories
     */
    fun getAllActiveCategories(): Flow<List<Category>>
    
    /**
     * Get all categories (including inactive)
     */
    fun getAllCategories(): Flow<List<Category>>
    
    /**
     * Get categories by type
     */
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
    
    /**
     * Get category by ID
     */
    suspend fun getCategoryById(categoryId: Long): Category?
    
    /**
     * Get category by name
     */
    suspend fun getCategoryByName(name: String): Category?
    
    /**
     * Get categories by type synchronously
     */
    suspend fun getCategoriesByTypeSync(type: CategoryType): List<Category>
    
    /**
     * Get default categories
     */
    fun getDefaultCategories(): Flow<List<Category>>
    
    /**
     * Search categories by name
     */
    fun searchCategories(searchQuery: String): Flow<List<Category>>
    
    /**
     * Get category summary
     */
    suspend fun getCategorySummary(): CategorySummary
    
    /**
     * Create a new category
     */
    suspend fun createCategory(category: Category): Result<Long>
    
    /**
     * Update an existing category
     */
    suspend fun updateCategory(category: Category): Result<Unit>
    
    /**
     * Delete a category
     */
    suspend fun deleteCategory(categoryId: Long): Result<Unit>
    
    /**
     * Delete a category by ID
     */
    suspend fun deleteCategoryById(categoryId: Long): Result<Unit>
    
    /**
     * Check if category name exists
     */
    suspend fun isCategoryNameExists(name: String, excludeId: Long = -1): Boolean
    
    /**
     * Initialize default categories
     */
    suspend fun initializeDefaultCategories(): Result<Unit>
}
