package com.example.moneyflow.utils

import com.example.moneyflow.R

/**
 * Utility object containing drawable resource IDs for category icons
 * These icons are available in the app's drawable resources
 */
object CategoryIcons {
    
    // Income category icons
    val income = mapOf(
        "salary" to R.drawable.money,
        "business" to R.drawable.wallet,
        "investment" to R.drawable.analysis,
        "freelance" to R.drawable.dollar,
        "rental" to R.drawable.home,
        "bonus" to R.drawable.money,
        "gift" to R.drawable.money,
        "refund" to R.drawable.dollar,
        "interest" to R.drawable.analysis,
        "other_income" to R.drawable.category_income
    )
    
    // Expense category icons
    val expense = mapOf(
        "food" to R.drawable.category_expense,
        "transport" to R.drawable.category_expense,
        "shopping" to R.drawable.category_expense,
        "entertainment" to R.drawable.category_expense,
        "health" to R.drawable.category_expense,
        "education" to R.drawable.lightbulb,
        "bills" to R.drawable.category_expense,
        "rent" to R.drawable.home,
        "groceries" to R.drawable.category_expense,
        "travel" to R.drawable.category_expense,
        "insurance" to R.drawable.category_expense,
        "gas" to R.drawable.category_expense,
        "restaurant" to R.drawable.category_expense,
        "clothing" to R.drawable.category_expense,
        "phone" to R.drawable.category_expense,
        "internet" to R.drawable.category_expense,
        "utilities" to R.drawable.category_expense,
        "maintenance" to R.drawable.category_expense,
        "charity" to R.drawable.category_expense,
        "other_expense" to R.drawable.category_expense
    )
    
    // Savings category icons
    val savings = mapOf(
        "emergency_fund" to R.drawable.savings,
        "retirement" to R.drawable.savings,
        "vacation" to R.drawable.category_saving,
        "house_fund" to R.drawable.home,
        "car_fund" to R.drawable.category_saving,
        "education_fund" to R.drawable.lightbulb,
        "investment_savings" to R.drawable.analysis,
        "goal_savings" to R.drawable.category_saving,
        "other_savings" to R.drawable.savings
    )
    
    // All category icons combined
    val allIcons = income + expense + savings
    
    /**
     * Get all income category icons
     */
    fun getIncomeIcons(): Map<String, Int> = income
    
    /**
     * Get all expense category icons
     */
    fun getExpenseIcons(): Map<String, Int> = expense
    
    /**
     * Get all savings category icons
     */
    fun getSavingsIcons(): Map<String, Int> = savings
    
    /**
     * Get all category icons as a list of pairs for UI
     */
    fun getAllIcons(): List<Pair<String, Int>> = allIcons.toList()
    
    /**
     * Get icon resource ID by name
     */
    fun getIconByName(iconName: String): Int? = allIcons[iconName]
    
    /**
     * Get default icon for category type
     */
    fun getDefaultIcon(type: String): Int {
        return when (type.lowercase()) {
            "income" -> R.drawable.money
            "expense" -> R.drawable.category_expense
            "savings" -> R.drawable.savings
            else -> R.drawable.category_expense
        }
    }
}
