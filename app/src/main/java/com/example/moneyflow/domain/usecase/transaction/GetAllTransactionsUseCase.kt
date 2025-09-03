package com.example.moneyflow.domain.usecase.transaction

import com.example.moneyflow.data.model.TransactionWithDetails
import com.example.moneyflow.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all transactions with details
 */
class GetAllTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<TransactionWithDetails>> {
        return transactionRepository.getAllTransactionsWithDetails()
    }
}
