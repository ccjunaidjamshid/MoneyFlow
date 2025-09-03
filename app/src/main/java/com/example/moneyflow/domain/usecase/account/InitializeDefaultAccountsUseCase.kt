package com.example.moneyflow.domain.usecase.account

import com.example.moneyflow.data.model.Account
import com.example.moneyflow.data.model.AccountType
import com.example.moneyflow.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for initializing default accounts on first app launch
 */
class InitializeDefaultAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        // Check if any accounts exist
        if (accountRepository.hasAnyAccounts()) {
            return Result.success(Unit) // Accounts already exist
        }
        
        // Create default accounts
        return accountRepository.createDefaultAccounts()
    }
}
