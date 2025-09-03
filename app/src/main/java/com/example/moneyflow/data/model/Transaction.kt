package com.example.moneyflow.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Transaction data class for expense tracking
 * Represents financial transactions with account and category relationships
 */
@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["categoryId"]),
        Index(value = ["transactionDate"]),
        Index(value = ["type"])
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val accountId: Long, // Reference to Account
    
    val categoryId: Long, // Reference to Category
    
    val amount: Double, // Transaction amount (always positive)
    
    val type: TransactionType, // Income, Expense, Transfer
    
    val description: String? = null, // Transaction reason/description
    
    val notes: String? = null, // Additional notes
    
    val transactionDate: Date = Date(), // When transaction occurred
    
    val location: String? = null, // Optional location information
    
    val tags: String? = null, // Comma-separated tags for additional categorization
    
    val receiptImagePath: String? = null, // Path to receipt image
    
    val isRecurring: Boolean = false, // Whether this is a recurring transaction
    
    val recurringFrequency: RecurringFrequency? = null, // How often it recurs
    
    val recurringEndDate: Date? = null, // When recurring ends
    
    val transferToAccountId: Long? = null, // For transfer transactions
    
    val exchangeRate: Double? = null, // For multi-currency transactions
    
    val originalCurrency: String? = null, // Original currency if converted
    
    val originalAmount: Double? = null, // Original amount before conversion
    
    val createdAt: Date = Date(),
    
    val updatedAt: Date = Date(),
    
    val isDeleted: Boolean = false // Soft delete flag
)

/**
 * Transaction types
 */
enum class TransactionType(val displayName: String) {
    INCOME("Income"),
    EXPENSE("Expense"),
    TRANSFER("Transfer")
}

/**
 * Recurring frequency options
 */
enum class RecurringFrequency(val displayName: String, val days: Int) {
    DAILY("Daily", 1),
    WEEKLY("Weekly", 7),
    BI_WEEKLY("Bi-Weekly", 14),
    MONTHLY("Monthly", 30),
    QUARTERLY("Quarterly", 90),
    YEARLY("Yearly", 365)
}

/**
 * Transaction with related entities for display
 */
data class TransactionWithDetails(
    val transaction: Transaction,
    val account: Account,
    val category: Category,
    val transferToAccount: Account? = null // For transfer transactions
)

/**
 * Transaction summary for dashboard and reports
 */
data class TransactionSummary(
    val totalTransactions: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val totalTransfers: Double,
    val netAmount: Double, // Income - Expenses
    val transactionsByType: Map<TransactionType, Int>,
    val recentTransactions: List<TransactionWithDetails>,
    val topExpenseCategories: List<CategoryExpenseSummary>,
    val topIncomeCategories: List<CategoryIncomeSummary>
)

/**
 * Category expense summary
 */
data class CategoryExpenseSummary(
    val category: Category,
    val totalAmount: Double,
    val transactionCount: Int,
    val percentage: Double // Percentage of total expenses
)

/**
 * Category income summary
 */
data class CategoryIncomeSummary(
    val category: Category,
    val totalAmount: Double,
    val transactionCount: Int,
    val percentage: Double // Percentage of total income
)

/**
 * Monthly transaction summary
 */
data class MonthlyTransactionSummary(
    val year: Int,
    val month: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val netAmount: Double,
    val transactionCount: Int
)

/**
 * Daily transaction summary
 */
data class DailyTransactionSummary(
    val date: Date,
    val totalIncome: Double,
    val totalExpenses: Double,
    val netAmount: Double,
    val transactionCount: Int
)
