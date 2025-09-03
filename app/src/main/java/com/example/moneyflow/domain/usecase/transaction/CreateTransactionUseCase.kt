package com.example.moneyflow.domain.usecase.transaction

import com.example.moneyflow.data.model.Transaction
import com.example.moneyflow.data.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for creating a new transaction
 */
class CreateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long {
        // Validate transaction data
        require(transaction.amount > 0) { "Transaction amount must be positive" }
        require(transaction.description?.isNotBlank() ?: true) { "Description cannot be empty if provided" }
        
        return transactionRepository.insertTransaction(transaction)
    }
}
