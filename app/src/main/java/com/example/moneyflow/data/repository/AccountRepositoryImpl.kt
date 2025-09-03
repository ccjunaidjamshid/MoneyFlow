package com.example.moneyflow.data.repository

import com.example.moneyflow.data.database.dao.AccountDao
import com.example.moneyflow.data.model.Account
import com.example.moneyflow.data.model.AccountSummary
import com.example.moneyflow.data.model.AccountType
import com.example.moneyflow.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AccountRepository interface
 * Handles all account-related data operations
 */
@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override suspend fun createAccount(account: Account): Result<Long> {
        return try {
            // Check if account name is unique
            val existingAccount = accountDao.getAccountByName(account.name)
            if (existingAccount != null) {
                return Result.failure(Exception("Account with this name already exists"))
            }
            
            val accountId = accountDao.insertAccount(
                account.copy(
                    createdAt = Date(),
                    updatedAt = Date()
                )
            )
            Result.success(accountId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAccount(account: Account): Result<Unit> {
        return try {
            // Check if account name is unique (excluding current account)
            val existingAccount = accountDao.getAccountByName(account.name)
            if (existingAccount != null && existingAccount.id != account.id) {
                return Result.failure(Exception("Account with this name already exists"))
            }
            
            accountDao.updateAccount(
                account.copy(updatedAt = Date())
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(accountId: Long): Result<Unit> {
        return try {
            // Check if account can be deleted (no pending transactions, etc.)
            if (!canDeleteAccount(accountId)) {
                return Result.failure(Exception("Cannot delete account with existing transactions"))
            }
            
            accountDao.deleteAccountById(accountId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountById(accountId: Long): Result<Account?> {
        return try {
            val account = accountDao.getAccountById(accountId)
            Result.success(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllActiveAccounts(): Flow<List<Account>> {
        return accountDao.getAllActiveAccounts()
    }

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts()
    }

    override fun getAccountByIdFlow(accountId: Long): Flow<Account?> {
        return accountDao.getAccountByIdFlow(accountId)
    }

    override fun getAccountsByType(type: AccountType): Flow<List<Account>> {
        return accountDao.getAccountsByType(type)
    }

    override suspend fun updateAccountBalance(accountId: Long, newBalance: Double): Result<Unit> {
        return try {
            accountDao.updateAccountBalance(accountId, newBalance, Date().time)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToAccountBalance(accountId: Long, amount: Double): Result<Unit> {
        return try {
            val account = accountDao.getAccountById(accountId)
                ?: return Result.failure(Exception("Account not found"))
            
            val newBalance = account.balance + amount
            accountDao.updateAccountBalance(accountId, newBalance, Date().time)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun subtractFromAccountBalance(accountId: Long, amount: Double): Result<Unit> {
        return try {
            val account = accountDao.getAccountById(accountId)
                ?: return Result.failure(Exception("Account not found"))
            
            val newBalance = account.balance - amount
            accountDao.updateAccountBalance(accountId, newBalance, Date().time)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTotalBalance(): Flow<Double> {
        return accountDao.getTotalBalanceFlow().map { it ?: 0.0 }
    }

    override suspend fun activateAccount(accountId: Long): Result<Unit> {
        return try {
            accountDao.activateAccount(accountId, Date().time)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deactivateAccount(accountId: Long): Result<Unit> {
        return try {
            accountDao.deactivateAccount(accountId, Date().time)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountSummary(): Result<AccountSummary> {
        return try {
            val totalBalance = accountDao.getTotalBalance() ?: 0.0
            val totalAccounts = accountDao.getTotalAccountCount()
            val activeAccounts = accountDao.getActiveAccountCount()
            
            // Get account counts by type
            val accountsByType = mutableMapOf<AccountType, Int>()
            AccountType.values().forEach { type ->
                // This would need a specific query for each type
                // For now, we'll implement it simply
                accountsByType[type] = 0
            }
            
            val summary = AccountSummary(
                totalBalance = totalBalance,
                totalAccounts = totalAccounts,
                activeAccounts = activeAccounts,
                accountsByType = accountsByType
            )
            
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchAccounts(query: String): Flow<List<Account>> {
        return accountDao.searchAccounts(query)
    }

    override suspend fun getAccountByName(name: String): Result<Account?> {
        return try {
            val account = accountDao.getAccountByName(name)
            Result.success(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAccountNameUnique(name: String, excludeId: Long?): Boolean {
        return try {
            val existingAccount = accountDao.getAccountByName(name)
            existingAccount == null || (excludeId != null && existingAccount.id == excludeId)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun canDeleteAccount(accountId: Long): Boolean {
        return try {
            // For now, always allow deletion
            // In a real app, you'd check for existing transactions
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun createDefaultAccounts(): Result<Unit> {
        return try {
            val defaultAccounts = listOf(
                Account(
                    name = "Cash",
                    accountType = AccountType.CASH,
                    balance = 0.0,
                    initialBalance = 0.0,
                    icon = "cash",
                    color = "#4CAF50",
                    description = "Cash in wallet"
                ),
                Account(
                    name = "Bank Account",
                    accountType = AccountType.BANK_ACCOUNT,
                    balance = 0.0,
                    initialBalance = 0.0,
                    icon = "bank",
                    color = "#2196F3",
                    description = "Primary bank account"
                ),
                Account(
                    name = "Credit Card",
                    accountType = AccountType.CREDIT_CARD,
                    balance = 0.0,
                    initialBalance = 0.0,
                    icon = "credit_card",
                    color = "#FF9800",
                    description = "Credit card account"
                )
            )
            
            accountDao.insertAccounts(defaultAccounts)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasAnyAccounts(): Boolean {
        return try {
            accountDao.getTotalAccountCount() > 0
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAllAccountsForBackup(): Result<List<Account>> {
        return try {
            val accounts = accountDao.getAllAccountsForBackup()
            Result.success(accounts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importAccounts(accounts: List<Account>): Result<Unit> {
        return try {
            accountDao.insertAccounts(accounts)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
