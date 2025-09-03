package com.example.moneyflow.domain.usecase.transaction

import com.example.moneyflow.data.model.Transaction
import com.example.moneyflow.data.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for updating an existing transaction
 */
class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(oldTransaction: Transaction, newTransaction: Transaction) {
        // Validate new transaction data
        require(newTransaction.amount > 0) { "Transaction amount must be positive" }
        require(newTransaction.description?.isNotBlank() ?: true) { "Description cannot be empty if provided" }
        require(oldTransaction.id == newTransaction.id) { "Transaction IDs must match" }
        
        transactionRepository.updateTransaction(oldTransaction, newTransaction)
    }
}
