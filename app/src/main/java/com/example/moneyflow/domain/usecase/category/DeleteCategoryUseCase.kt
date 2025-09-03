package com.example.moneyflow.domain.usecase.category

import com.example.moneyflow.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case to delete a category
 */
class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: Long): Result<Unit> {
        return try {
            categoryRepository.deleteCategoryById(categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
