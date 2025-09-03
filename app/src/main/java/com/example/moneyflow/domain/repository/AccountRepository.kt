package com.example.moneyflow.domain.repository

import com.example.moneyflow.data.model.Account
import com.example.moneyflow.data.model.AccountSummary
import com.example.moneyflow.data.model.AccountType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Account operations
 * Defines the contract for account data operations
 */
interface AccountRepository {
    
    // Basic CRUD operations
    suspend fun createAccount(account: Account): Result<Long>
    suspend fun updateAccount(account: Account): Result<Unit>
    suspend fun deleteAccount(accountId: Long): Result<Unit>
    suspend fun getAccountById(accountId: Long): Result<Account?>
    
    // Observe data changes
    fun getAllActiveAccounts(): Flow<List<Account>>
    fun getAllAccounts(): Flow<List<Account>>
    fun getAccountByIdFlow(accountId: Long): Flow<Account?>
    fun getAccountsByType(type: AccountType): Flow<List<Account>>
    
    // Balance operations
    suspend fun updateAccountBalance(accountId: Long, newBalance: Double): Result<Unit>
    suspend fun addToAccountBalance(accountId: Long, amount: Double): Result<Unit>
    suspend fun subtractFromAccountBalance(accountId: Long, amount: Double): Result<Unit>
    fun getTotalBalance(): Flow<Double>
    
    // Account management
    suspend fun activateAccount(accountId: Long): Result<Unit>
    suspend fun deactivateAccount(accountId: Long): Result<Unit>
    suspend fun getAccountSummary(): Result<AccountSummary>
    
    // Search and filter
    fun searchAccounts(query: String): Flow<List<Account>>
    suspend fun getAccountByName(name: String): Result<Account?>
    
    // Validation
    suspend fun isAccountNameUnique(name: String, excludeId: Long? = null): Boolean
    suspend fun canDeleteAccount(accountId: Long): Boolean
    
    // Default accounts
    suspend fun createDefaultAccounts(): Result<Unit>
    suspend fun hasAnyAccounts(): Boolean
    
    // Backup/Export
    suspend fun getAllAccountsForBackup(): Result<List<Account>>
    suspend fun importAccounts(accounts: List<Account>): Result<Unit>
}
