package com.example.moneyflow.domain.usecase.account

import com.example.moneyflow.data.model.Account
import com.example.moneyflow.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all active accounts
 */
class GetAllAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> {
        return accountRepository.getAllActiveAccounts()
    }
}
