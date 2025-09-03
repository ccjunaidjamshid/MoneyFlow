package com.example.moneyflow.domain.usecase.transaction

import com.example.moneyflow.data.model.TransactionSummary
import com.example.moneyflow.data.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for getting transaction summary
 */
class GetTransactionSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): TransactionSummary {
        return transactionRepository.getTransactionSummary()
    }
}
