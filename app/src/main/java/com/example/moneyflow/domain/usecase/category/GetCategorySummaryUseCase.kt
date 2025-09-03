package com.example.moneyflow.domain.usecase.category

import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.CategoryType
import com.example.moneyflow.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to get category summary with counts and types
 */
class GetCategorySummaryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    data class CategorySummary(
        val totalCategories: Int,
        val incomeCategories: Int,
        val expenseCategories: Int,
        val savingsCategories: Int,
        val mostUsedCategories: List<Category>
    )
    
    operator fun invoke(): Flow<CategorySummary> {
        return categoryRepository.getAllCategories().map { categories ->
            CategorySummary(
                totalCategories = categories.size,
                incomeCategories = categories.count { it.type == CategoryType.INCOME },
                expenseCategories = categories.count { it.type == CategoryType.EXPENSE },
                savingsCategories = categories.count { it.type == CategoryType.SAVINGS },
                mostUsedCategories = categories.take(5) // Top 5 categories
            )
        }
    }
}
