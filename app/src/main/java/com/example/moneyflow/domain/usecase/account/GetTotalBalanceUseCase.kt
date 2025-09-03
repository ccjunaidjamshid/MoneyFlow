package com.example.moneyflow.domain.usecase.account

import com.example.moneyflow.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting total balance across all accounts
 */
class GetTotalBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<Double> {
        return accountRepository.getTotalBalance()
    }
}
