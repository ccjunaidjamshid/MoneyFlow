package com.example.moneyflow.ui.theme

import androidx.annotation.DrawableRes
import com.example.moneyflow.R
import com.example.moneyflow.data.model.AccountType

/**
 * Icon mappings for different account types using drawable resources
 */
object AccountIcons {
    
    // Data class to hold icon information
    data class IconInfo(
        val name: String,
        @DrawableRes val drawableRes: Int
    )
    
    // General account icons using available drawable resources
    val generalIcons = listOf(
        IconInfo("wallet", R.drawable.wallet),
        IconInfo("account", R.drawable.account),
        IconInfo("card", R.drawable.card),
        IconInfo("savings", R.drawable.savings),
        IconInfo("money", R.drawable.money),
        IconInfo("wallet_account", R.drawable.wallet_account),
        IconInfo("dollar", R.drawable.dollar),
        IconInfo("budget", R.drawable.budget),
        IconInfo("analysis", R.drawable.analysis),
        IconInfo("category", R.drawable.category),
        IconInfo("home", R.drawable.home),
        IconInfo("social", R.drawable.social),
        IconInfo("lightbulb", R.drawable.lightbulb)
    )
    
    // Default icons for specific account types
    fun getDefaultIconForAccountType(accountType: AccountType): String {
        return when (accountType) {
            AccountType.CASH -> "money"
            AccountType.BANK_ACCOUNT -> "account"
            AccountType.CHECKING_ACCOUNT -> "account"
            AccountType.SAVINGS -> "savings"
            AccountType.CREDIT_CARD -> "card"
            AccountType.DEBIT_CARD -> "card"
            AccountType.INVESTMENT -> "analysis"
            AccountType.RETIREMENT -> "savings"
            AccountType.LOAN -> "account"
            AccountType.MORTGAGE -> "home"
            AccountType.E_WALLET -> "wallet"
            AccountType.PREPAID_CARD -> "card"
            AccountType.CRYPTO -> "dollar"
            AccountType.BUSINESS -> "budget"
            AccountType.JOINT_ACCOUNT -> "social"
            AccountType.GIFT_CARD -> "card"
            AccountType.OTHER -> "wallet"
        }
    }
    
    @DrawableRes
    fun getDrawableResourceByName(iconName: String): Int {
        return when (iconName) {
            "wallet" -> R.drawable.wallet
            "account" -> R.drawable.account
            "card" -> R.drawable.card
            "savings" -> R.drawable.savings
            "money" -> R.drawable.money
            "wallet_account" -> R.drawable.wallet_account
            "dollar" -> R.drawable.dollar
            "budget" -> R.drawable.budget
            "analysis" -> R.drawable.analysis
            "category" -> R.drawable.category
            "home" -> R.drawable.home
            "social" -> R.drawable.social
            "lightbulb" -> R.drawable.lightbulb
            else -> R.drawable.wallet
        }
    }
    
    fun getIconNameByDrawableRes(@DrawableRes drawableRes: Int): String {
        return when (drawableRes) {
            R.drawable.wallet -> "wallet"
            R.drawable.account -> "account"
            R.drawable.card -> "card"
            R.drawable.savings -> "savings"
            R.drawable.money -> "money"
            R.drawable.wallet_account -> "wallet_account"
            R.drawable.dollar -> "dollar"
            R.drawable.budget -> "budget"
            R.drawable.analysis -> "analysis"
            R.drawable.category -> "category"
            R.drawable.home -> "home"
            R.drawable.social -> "social"
            R.drawable.lightbulb -> "lightbulb"
            else -> "wallet"
        }
    }
    
    // Get all available icon names
    fun getAllIconNames(): List<String> {
        return generalIcons.map { it.name }
    }
}
