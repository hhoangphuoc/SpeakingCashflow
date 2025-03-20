package com.hhoangphuoc.speakingcashflow.repository

import com.google.firebase.Timestamp
import com.hhoangphuoc.speakingcashflow.data.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * This Transaction Repository storing the actions for executing transaction
 */
interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction): Result<Unit>
    suspend fun getTransactions(startDate: Timestamp, endDate: Timestamp): Flow<List<Transaction>>
    suspend fun deleteTransaction(transactionId: String): Result<Unit>
    suspend fun updateTransaction(transaction: Transaction): Result<Unit>
    suspend fun getTransactionsByMonth(year: Int, month: Int): Flow<List<Transaction>>
}