package com.example.moneyflow.domain.usecase.category

import com.example.moneyflow.data.model.Category
import com.example.moneyflow.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all active categories
 */
class GetAllCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getAllActiveCategories()
    }
}
