package com.example.moneyflow.data.database.dao

import androidx.room.*
import com.example.moneyflow.data.model.Account
import com.example.moneyflow.data.model.AccountType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Account entity
 */
@Dao
interface AccountDao {
    
    @Query("SELECT * FROM accounts WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveAccounts(): Flow<List<Account>>
    
    @Query("SELECT * FROM accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): Flow<List<Account>>
    
    @Query("SELECT * FROM accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: Long): Account?
    
    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun getAccountByIdFlow(accountId: Long): Flow<Account?>
    
    @Query("SELECT * FROM accounts WHERE name = :name LIMIT 1")
    suspend fun getAccountByName(name: String): Account?
    
    @Query("SELECT * FROM accounts WHERE accountType = :type AND isActive = 1")
    fun getAccountsByType(type: AccountType): Flow<List<Account>>
    
    @Query("SELECT SUM(balance) FROM accounts WHERE includeInTotalBalance = 1 AND isActive = 1")
    suspend fun getTotalBalance(): Double?
    
    @Query("SELECT SUM(balance) FROM accounts WHERE includeInTotalBalance = 1 AND isActive = 1")
    fun getTotalBalanceFlow(): Flow<Double?>
    
    @Query("SELECT COUNT(*) FROM accounts WHERE isActive = 1")
    suspend fun getActiveAccountCount(): Int
    
    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getTotalAccountCount(): Int
    
    // Note: This query will be implemented later when Transaction table is created
    // For now, we'll use a simpler approach without transaction statistics
    @Query("SELECT * FROM accounts WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAccountsWithStatsSimple(): Flow<List<Account>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<Account>): List<Long>
    
    @Update
    suspend fun updateAccount(account: Account)
    
    @Query("UPDATE accounts SET balance = :newBalance, updatedAt = :updatedAt WHERE id = :accountId")
    suspend fun updateAccountBalance(accountId: Long, newBalance: Double, updatedAt: Long)
    
    @Query("UPDATE accounts SET isActive = 0, updatedAt = :updatedAt WHERE id = :accountId")
    suspend fun deactivateAccount(accountId: Long, updatedAt: Long)
    
    @Query("UPDATE accounts SET isActive = 1, updatedAt = :updatedAt WHERE id = :accountId")
    suspend fun activateAccount(accountId: Long, updatedAt: Long)
    
    @Delete
    suspend fun deleteAccount(account: Account)
    
    @Query("DELETE FROM accounts WHERE id = :accountId")
    suspend fun deleteAccountById(accountId: Long)
    
    @Query("DELETE FROM accounts")
    suspend fun deleteAllAccounts()
    
    // Search functionality
    @Query("""
        SELECT * FROM accounts 
        WHERE (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        AND isActive = 1
        ORDER BY name ASC
    """)
    fun searchAccounts(query: String): Flow<List<Account>>
    
    // Backup/Export queries
    @Query("SELECT * FROM accounts ORDER BY id ASC")
    suspend fun getAllAccountsForBackup(): List<Account>
}
