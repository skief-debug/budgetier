package com.budgettracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a user-defined budget category / dynamic Zettel note card.
 * References spec.md Section 2.1
 */
@Entity(tableName = "category_budgets")
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,                // e.g., "Essen", "Auto", "Spar-Puffer"
    val limit: Double = 0.0,          // 0.0 means no limit set
    val type: String,                 // "AUSGABE" (expense deduction) or "RUECKLAGE" (savings/buffer)
    val position: Int,                // Order index for Drag & Drop UI sorting
    val isExcluded: Boolean = false,  // If true, excluded from main monthly total calculation
    val colorHex: String = "#00E676", // Visual card theme color (Hex string)
    val iconName: String = "default"  // Icon key for visual display
)
