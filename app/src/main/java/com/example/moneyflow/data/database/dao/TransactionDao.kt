package com.example.moneyflow.data.database.dao

import androidx.room.*
import com.example.moneyflow.data.model.*
import com.example.moneyflow.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for Transaction entity
 * Provides methods to interact with transactions in the database
 */
@Dao
interface TransactionDao {
    
    // Basic CRUD Operations
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long
    
    @Insert
    suspend fun insertTransactions(transactions: List<Transaction>): List<Long>
    
    @Update
    suspend fun updateTransaction(transaction: Transaction)
    
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
    
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)
    
    @Query("UPDATE transactions SET isDeleted = 1 WHERE id = :transactionId")
    suspend fun softDeleteTransaction(transactionId: Long)
    
    // Get Transactions
    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId AND isDeleted = 0")
    suspend fun getTransactionById(transactionId: Long): Transaction?
    
    @Query("SELECT * FROM transactions WHERE accountId = :accountId AND isDeleted = 0 ORDER BY transactionDate DESC")
    fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId AND isDeleted = 0 ORDER BY transactionDate DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE type = :type AND isDeleted = 0 ORDER BY transactionDate DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    
    // Transaction with Details (with JOINs)
    @Query("""
        SELECT * FROM transactions t
        WHERE t.isDeleted = 0
        ORDER BY t.transactionDate DESC
    """)
    fun getAllTransactionsWithDetails(): Flow<List<Transaction>>
    
    @Query("""
        SELECT * FROM transactions t
        WHERE t.id = :transactionId AND t.isDeleted = 0
    """)
    suspend fun getTransactionWithDetailsById(transactionId: Long): Transaction?
    
    @Query("""
        SELECT * FROM transactions t
        WHERE t.accountId = :accountId AND t.isDeleted = 0
        ORDER BY t.transactionDate DESC
        LIMIT :limit
    """)
    fun getRecentTransactionsByAccount(accountId: Long, limit: Int = 10): Flow<List<Transaction>>
    
    // Date Range Queries
    @Query("""
        SELECT * FROM transactions 
        WHERE transactionDate BETWEEN :startDate AND :endDate 
        AND isDeleted = 0 
        ORDER BY transactionDate DESC
    """)
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE transactionDate BETWEEN :startDate AND :endDate 
        AND isDeleted = 0 
        ORDER BY transactionDate DESC
    """)
    fun getTransactionsWithDetailsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>>
    
    // Summary Queries
    @Query("""
        SELECT 
            COUNT(*) as totalTransactions,
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as totalIncome,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as totalExpenses,
            COALESCE(SUM(CASE WHEN type = 'TRANSFER' THEN amount ELSE 0 END), 0) as totalTransfers
        FROM transactions 
        WHERE isDeleted = 0
    """)
    suspend fun getTransactionSummary(): TransactionSummaryRaw
    
    @Query("""
        SELECT 
            COUNT(*) as totalTransactions,
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as totalIncome,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as totalExpenses,
            COALESCE(SUM(CASE WHEN type = 'TRANSFER' THEN amount ELSE 0 END), 0) as totalTransfers
        FROM transactions 
        WHERE transactionDate BETWEEN :startDate AND :endDate
        AND isDeleted = 0
    """)
    suspend fun getTransactionSummaryByDateRange(startDate: Date, endDate: Date): TransactionSummaryRaw
    
    @Query("""
        SELECT 
            COUNT(*) as totalTransactions,
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as totalIncome,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as totalExpenses,
            COALESCE(SUM(CASE WHEN type = 'TRANSFER' THEN amount ELSE 0 END), 0) as totalTransfers
        FROM transactions 
        WHERE accountId = :accountId AND isDeleted = 0
    """)
    suspend fun getTransactionSummaryByAccount(accountId: Long): TransactionSummaryRaw
    
    // Category Summaries
    @Query("""
        SELECT c.*, 
               COALESCE(SUM(t.amount), 0) as totalAmount,
               COUNT(t.id) as transactionCount
        FROM categories c
        LEFT JOIN transactions t ON c.id = t.categoryId AND t.type = 'EXPENSE' AND t.isDeleted = 0
        WHERE c.type = 'EXPENSE' AND c.isActive = 1
        GROUP BY c.id
        ORDER BY totalAmount DESC
        LIMIT :limit
    """)
    suspend fun getTopExpenseCategories(limit: Int = 10): List<CategoryExpenseSummaryRaw>
    
    @Query("""
        SELECT c.*, 
               COALESCE(SUM(t.amount), 0) as totalAmount,
               COUNT(t.id) as transactionCount
        FROM categories c
        LEFT JOIN transactions t ON c.id = t.categoryId AND t.type = 'INCOME' AND t.isDeleted = 0
        WHERE c.type = 'INCOME' AND c.isActive = 1
        GROUP BY c.id
        ORDER BY totalAmount DESC
        LIMIT :limit
    """)
    suspend fun getTopIncomeCategories(limit: Int = 10): List<CategoryIncomeSummaryRaw>
    
    // Monthly Summaries
    @Query("""
        SELECT 
            strftime('%Y', transactionDate/1000, 'unixepoch') as year,
            strftime('%m', transactionDate/1000, 'unixepoch') as month,
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as totalIncome,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as totalExpenses,
            COUNT(*) as transactionCount
        FROM transactions 
        WHERE isDeleted = 0
        GROUP BY year, month
        ORDER BY year DESC, month DESC
    """)
    suspend fun getMonthlyTransactionSummaries(): List<MonthlyTransactionSummaryRaw>
    
    // Search Transactions
    @Query("""
        SELECT * FROM transactions t
        WHERE (t.description LIKE '%' || :query || '%' 
               OR t.notes LIKE '%' || :query || '%')
        AND t.isDeleted = 0
        ORDER BY t.transactionDate DESC
    """)
    fun searchTransactions(query: String): Flow<List<Transaction>>
    
    // Recurring Transactions
    @Query("SELECT * FROM transactions WHERE isRecurring = 1 AND isDeleted = 0")
    suspend fun getRecurringTransactions(): List<Transaction>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE isRecurring = 1 
        AND (recurringEndDate IS NULL OR recurringEndDate > :currentDate)
        AND isDeleted = 0
    """)
    suspend fun getActiveRecurringTransactions(currentDate: Date = Date()): List<Transaction>
}

// Raw data classes for query results
data class TransactionSummaryRaw(
    val totalTransactions: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val totalTransfers: Double
)

data class CategoryExpenseSummaryRaw(
    val id: Long,
    val name: String,
    val type: CategoryType,
    val iconName: String,
    val color: String,
    val totalAmount: Double,
    val transactionCount: Int
)

data class CategoryIncomeSummaryRaw(
    val id: Long,
    val name: String,
    val type: CategoryType,
    val iconName: String,
    val color: String,
    val totalAmount: Double,
    val transactionCount: Int
)

data class MonthlyTransactionSummaryRaw(
    val year: String,
    val month: String,
    val totalIncome: Double,
    val totalExpenses: Double,
    val transactionCount: Int
)
