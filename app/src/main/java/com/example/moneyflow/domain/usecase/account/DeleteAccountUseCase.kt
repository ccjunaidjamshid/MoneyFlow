package com.example.moneyflow.domain.usecase.account

import com.example.moneyflow.domain.repository.AccountRepository
import javax.inject.Inject

/**
 * Use case for deleting an account
 */
class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(accountId: Long): Result<Unit> {
        // Check if account can be deleted
        if (!accountRepository.canDeleteAccount(accountId)) {
            return Result.failure(Exception("Cannot delete account with existing transactions"))
        }
        
        return accountRepository.deleteAccount(accountId)
    }
}
