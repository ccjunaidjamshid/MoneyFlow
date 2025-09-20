package com.example.moneyflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.data.model.*
import com.example.moneyflow.domain.usecase.transaction.*
import com.example.moneyflow.domain.usecase.account.GetAllAccountsUseCase
import com.example.moneyflow.domain.usecase.account.InitializeDefaultAccountsUseCase
import com.example.moneyflow.domain.usecase.category.GetAllCategoriesUseCase
import com.example.moneyflow.domain.usecase.category.InitializeDefaultCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for Transaction operations
 * Manages transaction data and business logic for UI
 */
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val getTransactionSummaryUseCase: GetTransactionSummaryUseCase,
    private val createTransferUseCase: CreateTransferUseCase,
    private val searchTransactionsUseCase: SearchTransactionsUseCase,
    private val getAllAccountsUseCase: GetAllAccountsUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val initializeDefaultAccountsUseCase: InitializeDefaultAccountsUseCase,
    private val initializeDefaultCategoriesUseCase: InitializeDefaultCategoriesUseCase
) : ViewModel() {

    // State management
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionWithDetails>>(emptyList())
    val transactions: StateFlow<List<TransactionWithDetails>> = _transactions.asStateFlow()

    private val _transactionSummary = MutableStateFlow<TransactionSummary?>(null)
    val transactionSummary: StateFlow<TransactionSummary?> = _transactionSummary.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<TransactionWithDetails?>(null)
    val selectedTransaction: StateFlow<TransactionWithDetails?> = _selectedTransaction.asStateFlow()

    // Dialog states
    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _showTransferDialog = MutableStateFlow(false)
    val showTransferDialog: StateFlow<Boolean> = _showTransferDialog.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        initializeDefaultData()
        loadInitialData()
    }

    private fun initializeDefaultData() {
        viewModelScope.launch {
            try {
                Log.d("TransactionViewModel", "Initializing default data...")
                
                // Initialize default accounts first
                val accountResult = initializeDefaultAccountsUseCase()
                if (accountResult.isSuccess) {
                    Log.d("TransactionViewModel", "Default accounts initialized successfully")
                } else {
                    Log.e("TransactionViewModel", "Failed to initialize accounts: ${accountResult.exceptionOrNull()?.message}")
                }
                
                // Initialize default categories
                val categoryResult = initializeDefaultCategoriesUseCase()
                if (categoryResult.isSuccess) {
                    Log.d("TransactionViewModel", "Default categories initialized successfully")
                } else {
                    Log.e("TransactionViewModel", "Failed to initialize categories: ${categoryResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error during initialization", e)
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Load accounts and categories in parallel
                launch {
                    getAllAccountsUseCase().collect { accountList ->
                        _accounts.value = accountList
                    }
                }

                launch {
                    getAllCategoriesUseCase().collect { categoryList ->
                        _categories.value = categoryList
                    }
                }

                launch {
                    getAllTransactionsUseCase().collect { transactionList ->
                        _transactions.value = transactionList
                    }
                }

                // Load transaction summary
                loadTransactionSummary()

                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    private suspend fun loadTransactionSummary() {
        try {
            val summary = getTransactionSummaryUseCase()
            _transactionSummary.value = summary
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load transaction summary: ${e.message}"
            )
        }
    }

    // Transaction CRUD operations
    fun createTransaction(
        accountId: Long,
        categoryId: Long,
        amount: Double,
        type: TransactionType,
        description: String?,
        notes: String?,
        transactionDate: Date,
        location: String?,
        tags: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val transaction = Transaction(
                    accountId = accountId,
                    categoryId = categoryId,
                    amount = amount,
                    type = type,
                    description = description,
                    notes = notes,
                    transactionDate = transactionDate,
                    location = location,
                    tags = tags
                )

                createTransactionUseCase(transaction)
                loadTransactionSummary()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Transaction created successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create transaction"
                )
            }
        }
    }

    fun updateTransaction(
        transactionId: Long,
        accountId: Long,
        categoryId: Long,
        amount: Double,
        type: TransactionType,
        description: String?,
        notes: String?,
        transactionDate: Date,
        location: String?,
        tags: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val selectedTransaction = _selectedTransaction.value?.transaction
                if (selectedTransaction != null) {
                    val updatedTransaction = selectedTransaction.copy(
                        accountId = accountId,
                        categoryId = categoryId,
                        amount = amount,
                        type = type,
                        description = description,
                        notes = notes,
                        transactionDate = transactionDate,
                        location = location,
                        tags = tags,
                        updatedAt = Date()
                    )

                    updateTransactionUseCase(selectedTransaction, updatedTransaction)
                    loadTransactionSummary()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Transaction updated successfully"
                    )
                    hideEditDialog()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No transaction selected for update"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update transaction"
                )
            }
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                deleteTransactionUseCase(transactionId)
                loadTransactionSummary()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Transaction deleted successfully"
                )
                hideDeleteDialog()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete transaction"
                )
            }
        }
    }

    fun createTransfer(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        description: String?,
        transferCategoryId: Long,
        transactionDate: Date = Date()
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                createTransferUseCase(
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId,
                    amount = amount,
                    description = description,
                    transferCategoryId = transferCategoryId,
                    transactionDate = transactionDate
                )

                loadTransactionSummary()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Transfer created successfully"
                )
                hideTransferDialog()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create transfer"
                )
            }
        }
    }

    // Search functionality
    fun searchTransactions(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            // If query is empty, show all transactions
            viewModelScope.launch {
                getAllTransactionsUseCase().collect { transactionList ->
                    _transactions.value = transactionList
                }
            }
        } else {
            viewModelScope.launch {
                try {
                    searchTransactionsUseCase(query).collect { searchResults ->
                        _transactions.value = searchResults
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = "Search failed: ${e.message}"
                    )
                }
            }
        }
    }

    // Selection management
    fun selectTransaction(transaction: TransactionWithDetails) {
        _selectedTransaction.value = transaction
    }

    fun clearSelection() {
        _selectedTransaction.value = null
    }

    // Dialog management
    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
        clearSelection()
    }

    fun showEditDialog(transaction: TransactionWithDetails) {
        _selectedTransaction.value = transaction
        _showEditDialog.value = true
    }

    fun hideEditDialog() {
        _showEditDialog.value = false
        clearSelection()
    }

    fun showDeleteDialog(transaction: TransactionWithDetails) {
        _selectedTransaction.value = transaction
        _showDeleteDialog.value = true
    }

    fun hideDeleteDialog() {
        _showDeleteDialog.value = false
        clearSelection()
    }

    fun showTransferDialog() {
        _showTransferDialog.value = true
    }

    fun hideTransferDialog() {
        _showTransferDialog.value = false
    }

    // Error handling
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Fixed: Removed duplicate clearSuccessMessage function
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    // Utility functions
    fun getExpenseCategories(): List<Category> {
        return _categories.value.filter { it.type == CategoryType.EXPENSE }
    }

    fun getIncomeCategories(): List<Category> {
        return _categories.value.filter { it.type == CategoryType.INCOME }
    }

    fun getActiveAccounts(): List<Account> {
        return _accounts.value.filter { it.isActive }
    }
}

/**
 * UI State for Transaction screen
 */
data class TransactionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
) {

}