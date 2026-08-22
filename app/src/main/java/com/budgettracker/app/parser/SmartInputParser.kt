package com.budgettracker.app.parser

import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Result data class for parsed smart input.
 */
data class ParsedTransactionResult(
    val amount: Double,          // Negative value for expense
    val description: String,     // Extracted description string
    val dateMillis: Long,        // Timestamp in epoch milliseconds
    val categoryId: Int? = null  // Null for unassigned
)

/**
 * Smart Input Parser engine using deterministic regex rules.
 * References spec.md Section 4.1
 */
object SmartInputParser {

    private val NUMBER_REGEX = Regex("""\b\d+([.,]\d+)?\b""")
    private val GESTERN_REGEX = Regex("""\b(gestern|yesterday)\b""", RegexOption.IGNORE_CASE)
    private val HEUTE_REGEX = Regex("""\b(heute|today)\b""", RegexOption.IGNORE_CASE)

    /**
     * Parses a single text input string like "15 lidl" or "9,5 schuhe gestern" into a ParsedTransactionResult.
     * Returns null if no numeric amount could be parsed.
     */
    fun parseInput(rawInput: String): ParsedTransactionResult? {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return null

        // 1. Extract Amount via Regex
        val numberMatch = NUMBER_REGEX.find(trimmed) ?: return null
        val numberString = numberMatch.value.replace(',', '.')
        val parsedAmountValue = numberString.toDoubleOrNull() ?: return null

        // Expenses are stored as negative amounts
        val amount = -kotlin.math.abs(parsedAmountValue)

        // 2. Resolve Date
        val now = System.currentTimeMillis()
        val dateMillis = when {
            GESTERN_REGEX.containsMatchIn(trimmed) -> now - TimeUnit.DAYS.toMillis(1)
            else -> now
        }

        // 3. Extract Description
        var cleanedText = trimmed
            .removeRange(numberMatch.range)
            .replace(GESTERN_REGEX, "")
            .replace(HEUTE_REGEX, "")
            .replace(Regex("""\s+"""), " ")
            .trim()

        if (cleanedText.isEmpty()) {
            cleanedText = "Ausgabe"
        }

        return ParsedTransactionResult(
            amount = amount,
            description = cleanedText,
            dateMillis = dateMillis,
            categoryId = null
        )
    }
}
