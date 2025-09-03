package com.example.moneyflow.domain.usecase.category

import com.example.moneyflow.data.model.Category
import com.example.moneyflow.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case to create a new category
 */
class CreateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Long> {
        // Validate category data
        if (category.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Category name cannot be empty"))
        }
        
        if (category.name.length > 50) {
            return Result.failure(IllegalArgumentException("Category name cannot exceed 50 characters"))
        }
        
        return categoryRepository.createCategory(category)
    }
}
