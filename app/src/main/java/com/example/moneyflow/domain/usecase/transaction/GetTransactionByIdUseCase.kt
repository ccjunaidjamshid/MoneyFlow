package com.example.moneyflow.domain.usecase.transaction

import com.example.moneyflow.data.model.TransactionWithDetails
import com.example.moneyflow.data.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for getting a transaction by ID
 */
class GetTransactionByIdUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: Long): TransactionWithDetails? {
        require(transactionId > 0) { "Transaction ID must be valid" }
        
        return transactionRepository.getTransactionWithDetailsById(transactionId)
    }
}
