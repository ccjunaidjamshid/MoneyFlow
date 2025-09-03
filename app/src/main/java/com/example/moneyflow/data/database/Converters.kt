package com.example.moneyflow.data.database

import androidx.room.TypeConverter
import com.example.moneyflow.data.model.AccountType
import com.example.moneyflow.data.model.CategoryType
import com.example.moneyflow.data.model.TransactionType
import com.example.moneyflow.data.model.RecurringFrequency
import java.util.Date

/**
 * Type converters for Room database to handle custom data types
 */
class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromAccountType(accountType: AccountType): String {
        return accountType.name
    }

    @TypeConverter
    fun toAccountType(accountType: String): AccountType {
        return AccountType.valueOf(accountType)
    }

    @TypeConverter
    fun fromCategoryType(categoryType: CategoryType): String {
        return categoryType.name
    }

    @TypeConverter
    fun toCategoryType(categoryType: String): CategoryType {
        return CategoryType.valueOf(categoryType)
    }

    @TypeConverter
    fun fromTransactionType(transactionType: TransactionType): String {
        return transactionType.name
    }

    @TypeConverter
    fun toTransactionType(transactionType: String): TransactionType {
        return TransactionType.valueOf(transactionType)
    }

    @TypeConverter
    fun fromRecurringFrequency(frequency: RecurringFrequency?): String? {
        return frequency?.name
    }

    @TypeConverter
    fun toRecurringFrequency(frequency: String?): RecurringFrequency? {
        return frequency?.let { RecurringFrequency.valueOf(it) }
    }
}
