package com.example.moneyflow.domain.usecase.category

import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.CategoryType
import com.example.moneyflow.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get categories by type
 */
class GetCategoriesByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(type: CategoryType): Flow<List<Category>> {
        return categoryRepository.getCategoriesByType(type)
    }
}
