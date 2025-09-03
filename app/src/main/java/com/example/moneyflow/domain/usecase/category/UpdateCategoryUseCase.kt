package com.example.moneyflow.domain.usecase.category

import com.example.moneyflow.data.model.Category
import com.example.moneyflow.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case to update an existing category
 */
class UpdateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        // Validate category data
        if (category.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Category name cannot be empty"))
        }
        
        if (category.name.length > 50) {
            return Result.failure(IllegalArgumentException("Category name cannot exceed 50 characters"))
        }
        
        return categoryRepository.updateCategory(category)
    }
}
