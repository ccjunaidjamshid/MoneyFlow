package com.example.moneyflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Account data class for expense tracker
 * Represents user accounts like Cash, Bank Account, Credit Card, etc.
 */
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    val accountType: AccountType,
    
    val balance: Double,
    
    val initialBalance: Double,
    
    val currency: String = "USD",
    
    val icon: String, // Icon resource name or identifier
    
    val color: String, // Hex color code for account theme
    
    val description: String? = null,
    
    val isActive: Boolean = true,
    
    val createdAt: Date = Date(),
    
    val updatedAt: Date = Date(),
    
    val includeInTotalBalance: Boolean = true
)

/**
 * Account types for different kinds of accounts
 */
enum class AccountType(val displayName: String) {
    CASH("Cash"),
    BANK_ACCOUNT("Bank Account"),
    CHECKING_ACCOUNT("Checking Account"),
    SAVINGS("Savings Account"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    INVESTMENT("Investment"),
    RETIREMENT("Retirement"),
    LOAN("Loan"),
    MORTGAGE("Mortgage"),
    E_WALLET("E-Wallet"),
    PREPAID_CARD("Prepaid Card"),
    CRYPTO("Cryptocurrency"),
    BUSINESS("Business Account"),
    JOINT_ACCOUNT("Joint Account"),
    GIFT_CARD("Gift Card"),
    OTHER("Other")
}

/**
 * Account summary for dashboard display
 */
data class AccountSummary(
    val totalBalance: Double,
    val totalAccounts: Int,
    val activeAccounts: Int,
    val accountsByType: Map<AccountType, Int>
)
