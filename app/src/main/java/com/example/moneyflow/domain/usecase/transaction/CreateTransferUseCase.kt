package com.example.moneyflow.domain.usecase.transaction

import com.example.moneyflow.data.repository.TransactionRepository
import java.util.Date
import javax.inject.Inject

/**
 * Use case for creating a transfer between accounts
 */
class CreateTransferUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        description: String?,
        transferCategoryId: Long,
        transactionDate: Date = Date()
    ): Pair<Long, Long> {
        // Validate transfer data
        require(fromAccountId > 0) { "From account ID must be valid" }
        require(toAccountId > 0) { "To account ID must be valid" }
        require(fromAccountId != toAccountId) { "Cannot transfer to the same account" }
        require(amount > 0) { "Transfer amount must be positive" }
        require(transferCategoryId > 0) { "Transfer category ID must be valid" }
        
        return transactionRepository.createTransfer(
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            amount = amount,
            description = description,
            transferCategoryId = transferCategoryId,
            transactionDate = transactionDate
        )
    }
}
