package com.budgettracker.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing an individual transaction (expense or income).
 * References spec.md Section 2.2
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryBudget::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("categoryId"),
        Index("date")
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,               // Negative for expenses, positive for income
    val description: String,          // e.g., "Lidl", "Tanken", "Aral"
    val date: Long,                   // Timestamp in Epoch milliseconds
    val categoryId: Int?              // Foreign key to CategoryBudget. Null if "unassigned"
)
