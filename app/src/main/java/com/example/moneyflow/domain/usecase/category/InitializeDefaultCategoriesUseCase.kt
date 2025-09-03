package com.example.moneyflow.domain.usecase.category

import com.example.moneyflow.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case to initialize default categories if none exist
 */
class InitializeDefaultCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            categoryRepository.initializeDefaultCategories()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
