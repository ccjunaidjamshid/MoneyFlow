package com.example.moneyflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Category data class for transaction categorization
 * Represents categories like Food, Transportation, Salary, etc.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    val type: CategoryType,
    
    val iconName: String, // Icon resource name from drawable
    
    val color: String, // Hex color code for category theme
    
    val description: String? = null,
    
    val isActive: Boolean = true,
    
    val createdAt: Date = Date(),
    
    val updatedAt: Date = Date(),
    
    val isDefault: Boolean = false // System default categories
)

/**
 * Category types for different kinds of transactions
 */
enum class CategoryType(val displayName: String) {
    INCOME("Income"),
    EXPENSE("Expense"), 
    SAVINGS("Savings")
}

/**
 * Category summary for dashboard display
 */
data class CategorySummary(
    val totalCategories: Int,
    val activeCategories: Int,
    val categoriesByType: Map<CategoryType, Int>,
    val expenseCategories: List<Category>,
    val incomeCategories: List<Category>,
    val savingsCategories: List<Category>
)
