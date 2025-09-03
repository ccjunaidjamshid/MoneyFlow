package com.example.moneyflow.data.repository

import com.example.moneyflow.data.database.dao.TransactionDao
import com.example.moneyflow.data.database.dao.AccountDao
import com.example.moneyflow.data.database.dao.CategoryDao
import com.example.moneyflow.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Transaction operations
 * Handles business logic and data operations for transactions
 */
@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao
) {
    
    // Basic CRUD Operations
    suspend fun insertTransaction(transaction: Transaction): Long {
        val transactionId = transactionDao.insertTransaction(transaction)
        
        // Update account balance
        updateAccountBalance(transaction)
        
        return transactionId
    }
    
    suspend fun insertTransactions(transactions: List<Transaction>): List<Long> {
        val transactionIds = transactionDao.insertTransactions(transactions)
        
        // Update account balances for all transactions
        transactions.forEach { updateAccountBalance(it) }
        
        return transactionIds
    }
    
    suspend fun updateTransaction(oldTransaction: Transaction, newTransaction: Transaction) {
        // Revert old transaction effect on account balance
        revertAccountBalance(oldTransaction)
        
        // Apply new transaction effect
        updateAccountBalance(newTransaction)
        
        // Update the transaction
        transactionDao.updateTransaction(newTransaction)
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        // Revert transaction effect on account balance
        revertAccountBalance(transaction)
        
        // Soft delete the transaction
        transactionDao.softDeleteTransaction(transaction.id)
    }
    
    suspend fun deleteTransactionById(transactionId: Long) {
        val transaction = transactionDao.getTransactionById(transactionId)
        transaction?.let { deleteTransaction(it) }
    }
    
    // Get Transactions
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    suspend fun getTransactionById(transactionId: Long): Transaction? = 
        transactionDao.getTransactionById(transactionId)
    
    fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByAccount(accountId)
    
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByCategory(categoryId)
    
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByType(type)
    
    // Transaction with Details
    fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>> {
        return combine(
            transactionDao.getAllTransactionsWithDetails(),
            accountDao.getAllAccounts(),
            categoryDao.getAllCategories()
        ) { transactions, accounts, categories ->
            transactions.map { transaction ->
                val account = accounts.find { it.id == transaction.accountId }
                val category = categories.find { it.id == transaction.categoryId }
                val transferAccount = transaction.transferToAccountId?.let { id ->
                    accounts.find { it.id == id }
                }
                
                TransactionWithDetails(
                    transaction = transaction,
                    account = account ?: Account(id = 0, name = "Unknown", accountType = com.example.moneyflow.data.model.AccountType.CASH, balance = 0.0, initialBalance = 0.0, icon = "", color = ""),
                    category = category ?: Category(id = 0, name = "Unknown", type = com.example.moneyflow.data.model.CategoryType.EXPENSE, iconName = "", color = ""),
                    transferToAccount = transferAccount
                )
            }
        }
    }
    
    suspend fun getTransactionWithDetailsById(transactionId: Long): TransactionWithDetails? {
        val transaction = transactionDao.getTransactionWithDetailsById(transactionId) ?: return null
        val account = accountDao.getAccountById(transaction.accountId) ?: return null
        val category = categoryDao.getCategoryById(transaction.categoryId) ?: return null
        val transferAccount = transaction.transferToAccountId?.let { accountDao.getAccountById(it) }
        
        return TransactionWithDetails(
            transaction = transaction,
            account = account,
            category = category,
            transferToAccount = transferAccount
        )
    }
    
    fun getRecentTransactionsByAccount(accountId: Long, limit: Int = 10): Flow<List<TransactionWithDetails>> {
        return combine(
            transactionDao.getRecentTransactionsByAccount(accountId, limit),
            accountDao.getAllAccounts(),
            categoryDao.getAllCategories()
        ) { transactions, accounts, categories ->
            transactions.map { transaction ->
                val account = accounts.find { it.id == transaction.accountId }
                val category = categories.find { it.id == transaction.categoryId }
                val transferAccount = transaction.transferToAccountId?.let { id ->
                    accounts.find { it.id == id }
                }
                
                TransactionWithDetails(
                    transaction = transaction,
                    account = account ?: Account(id = 0, name = "Unknown", accountType = com.example.moneyflow.data.model.AccountType.CASH, balance = 0.0, initialBalance = 0.0, icon = "", color = ""),
                    category = category ?: Category(id = 0, name = "Unknown", type = com.example.moneyflow.data.model.CategoryType.EXPENSE, iconName = "", color = ""),
                    transferToAccount = transferAccount
                )
            }
        }
    }
    
    // Date Range Queries
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByDateRange(startDate, endDate)
    
    fun getTransactionsWithDetailsByDateRange(startDate: Date, endDate: Date): Flow<List<TransactionWithDetails>> {
        return combine(
            transactionDao.getTransactionsWithDetailsByDateRange(startDate, endDate),
            accountDao.getAllAccounts(),
            categoryDao.getAllCategories()
        ) { transactions, accounts, categories ->
            transactions.map { transaction ->
                val account = accounts.find { it.id == transaction.accountId }
                val category = categories.find { it.id == transaction.categoryId }
                val transferAccount = transaction.transferToAccountId?.let { id ->
                    accounts.find { it.id == id }
                }
                
                TransactionWithDetails(
                    transaction = transaction,
                    account = account ?: Account(id = 0, name = "Unknown", accountType = com.example.moneyflow.data.model.AccountType.CASH, balance = 0.0, initialBalance = 0.0, icon = "", color = ""),
                    category = category ?: Category(id = 0, name = "Unknown", type = com.example.moneyflow.data.model.CategoryType.EXPENSE, iconName = "", color = ""),
                    transferToAccount = transferAccount
                )
            }
        }
    }
    
    // Search
    fun searchTransactions(query: String): Flow<List<TransactionWithDetails>> {
        return combine(
            transactionDao.searchTransactions(query),
            accountDao.getAllAccounts(),
            categoryDao.getAllCategories()
        ) { transactions, accounts, categories ->
            transactions.map { transaction ->
                val account = accounts.find { it.id == transaction.accountId }
                val category = categories.find { it.id == transaction.categoryId }
                val transferAccount = transaction.transferToAccountId?.let { id ->
                    accounts.find { it.id == id }
                }
                
                TransactionWithDetails(
                    transaction = transaction,
                    account = account ?: Account(id = 0, name = "Unknown", accountType = com.example.moneyflow.data.model.AccountType.CASH, balance = 0.0, initialBalance = 0.0, icon = "", color = ""),
                    category = category ?: Category(id = 0, name = "Unknown", type = com.example.moneyflow.data.model.CategoryType.EXPENSE, iconName = "", color = ""),
                    transferToAccount = transferAccount
                )
            }
        }
    }
    
    // Summary Operations
    suspend fun getTransactionSummary(): TransactionSummary {
        val summaryRaw = transactionDao.getTransactionSummary()
        val rawTransactions = transactionDao.getAllTransactionsWithDetails().first().take(10)
        val recentTransactions = convertTransactionsToDetails(rawTransactions)
        val topExpenseCategories = transactionDao.getTopExpenseCategories(5)
        val topIncomeCategories = transactionDao.getTopIncomeCategories(5)
        
        val totalExpenses = summaryRaw.totalExpenses
        val totalIncome = summaryRaw.totalIncome
        
        return TransactionSummary(
            totalTransactions = summaryRaw.totalTransactions,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            totalTransfers = summaryRaw.totalTransfers,
            netAmount = totalIncome - totalExpenses,
            transactionsByType = mapOf(
                TransactionType.INCOME to 0, // You can add these counts if needed
                TransactionType.EXPENSE to 0,
                TransactionType.TRANSFER to 0
            ),
            recentTransactions = recentTransactions,
            topExpenseCategories = topExpenseCategories.map { raw ->
                CategoryExpenseSummary(
                    category = Category(
                        id = raw.id,
                        name = raw.name,
                        type = raw.type,
                        iconName = raw.iconName,
                        color = raw.color
                    ),
                    totalAmount = raw.totalAmount,
                    transactionCount = raw.transactionCount,
                    percentage = if (totalExpenses > 0) (raw.totalAmount / totalExpenses) * 100 else 0.0
                )
            },
            topIncomeCategories = topIncomeCategories.map { raw ->
                CategoryIncomeSummary(
                    category = Category(
                        id = raw.id,
                        name = raw.name,
                        type = raw.type,
                        iconName = raw.iconName,
                        color = raw.color
                    ),
                    totalAmount = raw.totalAmount,
                    transactionCount = raw.transactionCount,
                    percentage = if (totalIncome > 0) (raw.totalAmount / totalIncome) * 100 else 0.0
                )
            }
        )
    }
    
    suspend fun getTransactionSummaryByDateRange(startDate: Date, endDate: Date): TransactionSummary {
        val summaryRaw = transactionDao.getTransactionSummaryByDateRange(startDate, endDate)
        val rawTransactions = transactionDao.getTransactionsWithDetailsByDateRange(startDate, endDate).first().take(10)
        val recentTransactions = convertTransactionsToDetails(rawTransactions)
        
        val totalExpenses = summaryRaw.totalExpenses
        val totalIncome = summaryRaw.totalIncome
        
        return TransactionSummary(
            totalTransactions = summaryRaw.totalTransactions,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            totalTransfers = summaryRaw.totalTransfers,
            netAmount = totalIncome - totalExpenses,
            transactionsByType = mapOf(
                TransactionType.INCOME to 0,
                TransactionType.EXPENSE to 0,
                TransactionType.TRANSFER to 0
            ),
            recentTransactions = recentTransactions,
            topExpenseCategories = emptyList(), // Can be implemented if needed
            topIncomeCategories = emptyList()
        )
    }
    
    suspend fun getTransactionSummaryByAccount(accountId: Long): TransactionSummary {
        val summaryRaw = transactionDao.getTransactionSummaryByAccount(accountId)
        val rawTransactions = transactionDao.getRecentTransactionsByAccount(accountId, 10).first()
        val recentTransactions = convertTransactionsToDetails(rawTransactions)
        
        val totalExpenses = summaryRaw.totalExpenses
        val totalIncome = summaryRaw.totalIncome
        
        return TransactionSummary(
            totalTransactions = summaryRaw.totalTransactions,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            totalTransfers = summaryRaw.totalTransfers,
            netAmount = totalIncome - totalExpenses,
            transactionsByType = mapOf(
                TransactionType.INCOME to 0,
                TransactionType.EXPENSE to 0,
                TransactionType.TRANSFER to 0
            ),
            recentTransactions = recentTransactions,
            topExpenseCategories = emptyList(),
            topIncomeCategories = emptyList()
        )
    }
    
    // Monthly Summaries
    suspend fun getMonthlyTransactionSummaries(): List<MonthlyTransactionSummary> {
        val summaries = transactionDao.getMonthlyTransactionSummaries()
        return summaries.map { raw ->
            MonthlyTransactionSummary(
                year = raw.year.toInt(),
                month = raw.month.toInt(),
                totalIncome = raw.totalIncome,
                totalExpenses = raw.totalExpenses,
                netAmount = raw.totalIncome - raw.totalExpenses,
                transactionCount = raw.transactionCount
            )
        }
    }
    
    // Recurring Transactions
    suspend fun getRecurringTransactions(): List<Transaction> = 
        transactionDao.getRecurringTransactions()
    
    suspend fun getActiveRecurringTransactions(): List<Transaction> = 
        transactionDao.getActiveRecurringTransactions()
    
    // Transfer Transaction
    suspend fun createTransfer(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        description: String?,
        transferCategoryId: Long,
        transactionDate: Date = Date()
    ): Pair<Long, Long> {
        // Create transfer out transaction
        val transferOutTransaction = Transaction(
            accountId = fromAccountId,
            categoryId = transferCategoryId,
            amount = amount,
            type = TransactionType.TRANSFER,
            description = description,
            transferToAccountId = toAccountId,
            transactionDate = transactionDate
        )
        
        // Create transfer in transaction
        val transferInTransaction = Transaction(
            accountId = toAccountId,
            categoryId = transferCategoryId,
            amount = amount,
            type = TransactionType.TRANSFER,
            description = description,
            transferToAccountId = fromAccountId,
            transactionDate = transactionDate
        )
        
        val transferOutId = insertTransaction(transferOutTransaction)
        val transferInId = insertTransaction(transferInTransaction)
        
        return Pair(transferOutId, transferInId)
    }
    
    // Private helper methods
    private suspend fun updateAccountBalance(transaction: Transaction) {
        val account = accountDao.getAccountById(transaction.accountId) ?: return
        
        val newBalance = when (transaction.type) {
            TransactionType.INCOME -> account.balance + transaction.amount
            TransactionType.EXPENSE -> account.balance - transaction.amount
            TransactionType.TRANSFER -> {
                if (transaction.transferToAccountId != null) {
                    // This is a transfer out, subtract from balance
                    account.balance - transaction.amount
                } else {
                    // This is a transfer in, add to balance
                    account.balance + transaction.amount
                }
            }
        }
        
        val updatedAccount = account.copy(
            balance = newBalance,
            updatedAt = Date()
        )
        
        accountDao.updateAccount(updatedAccount)
    }
    
    private suspend fun revertAccountBalance(transaction: Transaction) {
        val account = accountDao.getAccountById(transaction.accountId) ?: return
        
        val newBalance = when (transaction.type) {
            TransactionType.INCOME -> account.balance - transaction.amount
            TransactionType.EXPENSE -> account.balance + transaction.amount
            TransactionType.TRANSFER -> {
                if (transaction.transferToAccountId != null) {
                    // This was a transfer out, add back to balance
                    account.balance + transaction.amount
                } else {
                    // This was a transfer in, subtract from balance
                    account.balance - transaction.amount
                }
            }
        }
        
        val updatedAccount = account.copy(
            balance = newBalance,
            updatedAt = Date()
        )
        
        accountDao.updateAccount(updatedAccount)
    }
    
    private suspend fun convertTransactionsToDetails(transactions: List<Transaction>): List<TransactionWithDetails> {
        val allAccounts = accountDao.getAllAccounts().first()
        val allCategories = categoryDao.getAllCategories().first()
        
        return transactions.map { transaction ->
            val account = allAccounts.find { it.id == transaction.accountId }
                ?: throw IllegalStateException("Account not found for transaction ${transaction.id}")
            val category = allCategories.find { it.id == transaction.categoryId }
                ?: throw IllegalStateException("Category not found for transaction ${transaction.id}")
            val transferAccount = transaction.transferToAccountId?.let { id ->
                allAccounts.find { it.id == id }
            }
            
            TransactionWithDetails(
                transaction = transaction,
                account = account,
                category = category,
                transferToAccount = transferAccount
            )
        }
    }
}
