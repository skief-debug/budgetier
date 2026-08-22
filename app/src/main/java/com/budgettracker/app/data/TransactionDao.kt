package com.budgettracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Transaction entities providing full CRUD operations.
 */
@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsList(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategoryFlow(categoryId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE categoryId IS NULL ORDER BY date DESC")
    fun getUnassignedTransactionsFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE categoryId IS NULL ORDER BY date DESC")
    suspend fun getUnassignedTransactionsList(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE date >= :startDateMillis AND date <= :endDateMillis ORDER BY date DESC")
    fun getTransactionsInRangeFlow(startDateMillis: Long, endDateMillis: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}
