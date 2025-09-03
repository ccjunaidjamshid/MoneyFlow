package com.example.moneyflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.CategoryType
import com.example.moneyflow.domain.usecase.category.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing category operations
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getCategoriesByTypeUseCase: GetCategoriesByTypeUseCase,
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getCategorySummaryUseCase: GetCategorySummaryUseCase,
    private val initializeDefaultCategoriesUseCase: InitializeDefaultCategoriesUseCase
) : ViewModel() {

    // State flows for UI
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _categorySummary = MutableStateFlow<GetCategorySummaryUseCase.CategorySummary?>(null)
    val categorySummary: StateFlow<GetCategorySummaryUseCase.CategorySummary?> = _categorySummary.asStateFlow()

    // Dialog states
    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    // Filter state
    private val _selectedCategoryType = MutableStateFlow<CategoryType?>(null)
    val selectedCategoryType: StateFlow<CategoryType?> = _selectedCategoryType.asStateFlow()

    init {
        // Initialize default categories and load all categories
        initializeCategories()
        loadCategories()
        loadCategorySummary()
    }

    private fun initializeCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                initializeDefaultCategoriesUseCase()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to initialize categories: ${e.message}"
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _selectedCategoryType.value?.let { type ->
                getCategoriesByTypeUseCase(type).collect { categoryList ->
                    _categories.value = categoryList
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } ?: run {
                getAllCategoriesUseCase().collect { categoryList ->
                    _categories.value = categoryList
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    private fun loadCategorySummary() {
        viewModelScope.launch {
            getCategorySummaryUseCase().collect { summary ->
                _categorySummary.value = summary
            }
        }
    }

    fun filterByType(type: CategoryType?) {
        _selectedCategoryType.value = type
        loadCategories()
    }

    fun createCategory(name: String, type: CategoryType, iconName: String, color: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val category = Category(
                name = name.trim(),
                type = type,
                iconName = iconName,
                color = color
            )
            
            createCategoryUseCase(category)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Category created successfully"
                    )
                    _showCreateDialog.value = false
                    clearMessages()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to create category"
                    )
                }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            updateCategoryUseCase(category)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Category updated successfully"
                    )
                    _showEditDialog.value = false
                    _selectedCategory.value = null
                    clearMessages()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to update category"
                    )
                }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            deleteCategoryUseCase(categoryId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Category deleted successfully"
                    )
                    _showDeleteDialog.value = false
                    _selectedCategory.value = null
                    clearMessages()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to delete category"
                    )
                }
        }
    }

    // Dialog management functions
    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }

    fun showEditDialog(category: Category) {
        _selectedCategory.value = category
        _showEditDialog.value = true
    }

    fun hideEditDialog() {
        _showEditDialog.value = false
        _selectedCategory.value = null
    }

    fun showDeleteDialog(category: Category) {
        _selectedCategory.value = category
        _showDeleteDialog.value = true
    }

    fun hideDeleteDialog() {
        _showDeleteDialog.value = false
        _selectedCategory.value = null
    }

    private fun clearMessages() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000) // Clear messages after 3 seconds
            _uiState.value = _uiState.value.copy(
                successMessage = null,
                errorMessage = null
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}

/**
 * UI State for Category operations
 */
data class CategoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
