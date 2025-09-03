package com.example.moneyflow.domain.usecase.account

import com.example.moneyflow.data.model.Account
import com.example.moneyflow.domain.repository.AccountRepository
import javax.inject.Inject

/**
 * Use case for creating a new account
 */
class CreateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Result<Long> {
        // Validate account data
        if (account.name.isBlank()) {
            return Result.failure(Exception("Account name cannot be empty"))
        }
        
        if (account.name.length > 50) {
            return Result.failure(Exception("Account name is too long"))
        }
        
        // Check if name is unique
        if (!accountRepository.isAccountNameUnique(account.name)) {
            return Result.failure(Exception("Account name already exists"))
        }
        
        return accountRepository.createAccount(account)
    }
}
