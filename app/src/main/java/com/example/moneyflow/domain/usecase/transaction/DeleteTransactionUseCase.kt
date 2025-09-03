package com.example.moneyflow.domain.usecase.transaction

import com.example.moneyflow.data.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for deleting a transaction
 */
class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: Long) {
        require(transactionId > 0) { "Transaction ID must be valid" }
        
        transactionRepository.deleteTransactionById(transactionId)
    }
}
