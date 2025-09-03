package com.example.moneyflow.data.database.dao

import androidx.room.*
import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.CategoryType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Category entity
 */
@Dao
interface CategoryDao {
    
    /**
     * Get all categories ordered by creation date
     */
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveCategories(): Flow<List<Category>>
    
    /**
     * Get all categories (including inactive)
     */
    @Query("SELECT * FROM categories ORDER BY createdAt DESC")
    fun getAllCategories(): Flow<List<Category>>
    
    /**
     * Get categories by type
     */
    @Query("SELECT * FROM categories WHERE type = :type AND isActive = 1 ORDER BY name ASC")
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
    
    /**
     * Get category by ID
     */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): Category?
    
    /**
     * Get category by name
     */
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): Category?
    
    /**
     * Get categories by type synchronously
     */
    @Query("SELECT * FROM categories WHERE type = :type AND isActive = 1 ORDER BY name ASC")
    suspend fun getCategoriesByTypeSync(type: CategoryType): List<Category>
    
    /**
     * Get default categories
     */
    @Query("SELECT * FROM categories WHERE isDefault = 1 AND isActive = 1 ORDER BY type, name ASC")
    fun getDefaultCategories(): Flow<List<Category>>
    
    /**
     * Get categories count by type
     */
    @Query("SELECT COUNT(*) FROM categories WHERE type = :type AND isActive = 1")
    suspend fun getCategoryCountByType(type: CategoryType): Int
    
    /**
     * Search categories by name
     */
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :searchQuery || '%' AND isActive = 1 ORDER BY name ASC")
    fun searchCategories(searchQuery: String): Flow<List<Category>>
    
    /**
     * Insert a new category
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    /**
     * Insert multiple categories
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)
    
    /**
     * Update an existing category
     */
    @Update
    suspend fun updateCategory(category: Category)
    
    /**
     * Delete a category
     */
    @Delete
    suspend fun deleteCategory(category: Category)
    
    /**
     * Delete a category by ID
     */
    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: Long)
    
    /**
     * Soft delete a category (mark as inactive)
     */
    @Query("UPDATE categories SET isActive = 0, updatedAt = :updatedAt WHERE id = :categoryId")
    suspend fun softDeleteCategory(categoryId: Long, updatedAt: java.util.Date = java.util.Date())
    
    /**
     * Check if category name exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM categories WHERE name = :name AND id != :excludeId)")
    suspend fun isCategoryNameExists(name: String, excludeId: Long = -1): Boolean
    
    /**
     * Get total categories count
     */
    @Query("SELECT COUNT(*) FROM categories WHERE isActive = 1")
    suspend fun getTotalCategoriesCount(): Int
}
