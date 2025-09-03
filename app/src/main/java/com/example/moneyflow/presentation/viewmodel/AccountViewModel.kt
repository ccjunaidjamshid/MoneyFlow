package com.example.moneyflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.data.model.Account
import com.example.moneyflow.data.model.AccountType
import com.example.moneyflow.domain.usecase.account.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Account management screens
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAllAccountsUseCase: GetAllAccountsUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getTotalBalanceUseCase: GetTotalBalanceUseCase,
    private val initializeDefaultAccountsUseCase: InitializeDefaultAccountsUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    // Accounts
    val accounts = getAllAccountsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Total Balance
    val totalBalance = getTotalBalanceUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    init {
        // No default accounts initialization - start with empty state
    }

    fun showCreateAccountDialog() {
        _uiState.value = _uiState.value.copy(
            showCreateDialog = true,
            createAccountForm = CreateAccountForm()
        )
    }

    fun hideCreateAccountDialog() {
        _uiState.value = _uiState.value.copy(
            showCreateDialog = false,
            createAccountForm = CreateAccountForm(),
            isCreating = false,
            createError = null
        )
    }

    fun updateAccountName(name: String) {
        _uiState.value = _uiState.value.copy(
            createAccountForm = _uiState.value.createAccountForm.copy(name = name),
            createError = null
        )
    }

    fun updateAccountType(type: AccountType) {
        _uiState.value = _uiState.value.copy(
            createAccountForm = _uiState.value.createAccountForm.copy(accountType = type)
        )
    }

    fun updateInitialBalance(balance: String) {
        val balanceValue = balance.toDoubleOrNull() ?: 0.0
        _uiState.value = _uiState.value.copy(
            createAccountForm = _uiState.value.createAccountForm.copy(
                initialBalance = balance,
                initialBalanceValue = balanceValue
            )
        )
    }

    fun updateAccountIcon(icon: String) {
        _uiState.value = _uiState.value.copy(
            createAccountForm = _uiState.value.createAccountForm.copy(icon = icon)
        )
    }

    fun updateAccountColor(color: String) {
        _uiState.value = _uiState.value.copy(
            createAccountForm = _uiState.value.createAccountForm.copy(color = color)
        )
    }

    fun updateAccountDescription(description: String) {
        _uiState.value = _uiState.value.copy(
            createAccountForm = _uiState.value.createAccountForm.copy(description = description)
        )
    }

    fun createAccount() {
        val form = _uiState.value.createAccountForm
        
        // Validation
        if (form.name.isBlank()) {
            _uiState.value = _uiState.value.copy(createError = "Account name is required")
            return
        }

        _uiState.value = _uiState.value.copy(isCreating = true, createError = null)

        viewModelScope.launch {
            val account = Account(
                name = form.name.trim(),
                accountType = form.accountType,
                balance = form.initialBalanceValue,
                initialBalance = form.initialBalanceValue,
                icon = form.icon,
                color = form.color,
                description = form.description.takeIf { it.isNotBlank() }
            )

            createAccountUseCase(account)
                .onSuccess {
                    hideCreateAccountDialog()
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Account created successfully!"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        createError = error.message ?: "Failed to create account"
                    )
                }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    // Edit Account functionality
    fun showEditAccountDialog(account: Account) {
        _uiState.value = _uiState.value.copy(
            showEditDialog = true,
            editingAccount = account,
            editAccountForm = CreateAccountForm(
                name = account.name,
                accountType = account.accountType,
                initialBalance = account.balance.toString(),
                initialBalanceValue = account.balance,
                icon = account.icon,
                color = account.color,
                description = account.description ?: ""
            )
        )
    }

    fun hideEditAccountDialog() {
        _uiState.value = _uiState.value.copy(
            showEditDialog = false,
            editingAccount = null,
            editAccountForm = CreateAccountForm(),
            isEditing = false,
            editError = null
        )
    }

    fun updateEditAccountName(name: String) {
        _uiState.value = _uiState.value.copy(
            editAccountForm = _uiState.value.editAccountForm.copy(name = name),
            editError = null
        )
    }

    fun updateEditAccountType(type: AccountType) {
        _uiState.value = _uiState.value.copy(
            editAccountForm = _uiState.value.editAccountForm.copy(accountType = type)
        )
    }

    fun updateEditInitialBalance(balance: String) {
        val balanceValue = balance.toDoubleOrNull() ?: 0.0
        _uiState.value = _uiState.value.copy(
            editAccountForm = _uiState.value.editAccountForm.copy(
                initialBalance = balance,
                initialBalanceValue = balanceValue
            )
        )
    }

    fun updateEditAccountIcon(icon: String) {
        _uiState.value = _uiState.value.copy(
            editAccountForm = _uiState.value.editAccountForm.copy(icon = icon)
        )
    }

    fun updateEditAccountColor(color: String) {
        _uiState.value = _uiState.value.copy(
            editAccountForm = _uiState.value.editAccountForm.copy(color = color)
        )
    }

    fun updateEditAccountDescription(description: String) {
        _uiState.value = _uiState.value.copy(
            editAccountForm = _uiState.value.editAccountForm.copy(description = description)
        )
    }

    fun updateAccount() {
        val form = _uiState.value.editAccountForm
        val originalAccount = _uiState.value.editingAccount ?: return
        
        // Validation
        if (form.name.isBlank()) {
            _uiState.value = _uiState.value.copy(editError = "Account name is required")
            return
        }

        _uiState.value = _uiState.value.copy(isEditing = true, editError = null)

        viewModelScope.launch {
            val updatedAccount = originalAccount.copy(
                name = form.name.trim(),
                accountType = form.accountType,
                balance = form.initialBalanceValue,
                initialBalance = form.initialBalanceValue,
                icon = form.icon,
                color = form.color,
                description = form.description.takeIf { it.isNotBlank() },
                updatedAt = java.util.Date()
            )

            updateAccountUseCase(updatedAccount)
                .onSuccess {
                    hideEditAccountDialog()
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Account updated successfully!"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isEditing = false,
                        editError = error.message ?: "Failed to update account"
                    )
                }
        }
    }

    // Delete Account functionality
    fun showDeleteConfirmDialog(account: Account) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            accountToDelete = account
        )
    }

    fun hideDeleteConfirmDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            accountToDelete = null,
            isDeleting = false,
            deleteError = null
        )
    }

    fun deleteAccount() {
        val account = _uiState.value.accountToDelete ?: return
        
        _uiState.value = _uiState.value.copy(isDeleting = true, deleteError = null)

        viewModelScope.launch {
            deleteAccountUseCase(account.id)
                .onSuccess {
                    hideDeleteConfirmDialog()
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Account deleted successfully!"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        deleteError = error.message ?: "Failed to delete account"
                    )
                }
        }
    }

    // Action menu functionality
    fun showAccountActionMenu(account: Account) {
        _uiState.value = _uiState.value.copy(
            showActionMenu = true,
            selectedAccount = account
        )
    }

    fun hideAccountActionMenu() {
        _uiState.value = _uiState.value.copy(
            showActionMenu = false,
            selectedAccount = null
        )
    }
}

/**
 * UI State for Account screen
 */
data class AccountUiState(
    val showCreateDialog: Boolean = false,
    val createAccountForm: CreateAccountForm = CreateAccountForm(),
    val isCreating: Boolean = false,
    val createError: String? = null,
    val successMessage: String? = null,
    
    // Edit functionality
    val showEditDialog: Boolean = false,
    val editingAccount: Account? = null,
    val editAccountForm: CreateAccountForm = CreateAccountForm(),
    val isEditing: Boolean = false,
    val editError: String? = null,
    
    // Delete functionality
    val showDeleteDialog: Boolean = false,
    val accountToDelete: Account? = null,
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    
    // Action menu
    val showActionMenu: Boolean = false,
    val selectedAccount: Account? = null
)

/**
 * Form state for creating account
 */
data class CreateAccountForm(
    val name: String = "",
    val accountType: AccountType = AccountType.CASH,
    val initialBalance: String = "0",
    val initialBalanceValue: Double = 0.0,
    val icon: String = "money",
    val color: String = "#2196F3",
    val description: String = ""
)
