package com.example.moneyflow.domain.usecase.transaction

import com.example.moneyflow.data.model.TransactionWithDetails
import com.example.moneyflow.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching transactions
 */
class SearchTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(query: String): Flow<List<TransactionWithDetails>> {
        require(query.isNotBlank()) { "Search query cannot be empty" }
        
        return transactionRepository.searchTransactions(query.trim())
    }
}
