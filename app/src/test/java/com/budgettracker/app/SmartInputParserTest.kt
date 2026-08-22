package com.budgettracker.app

import com.budgettracker.app.parser.SmartInputParser
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class SmartInputParserTest {

    @Test
    fun testParseSimpleAmountAndDescription() {
        val result = SmartInputParser.parseInput("15 lidl")
        assertNotNull(result)
        assertEquals(-15.0, result!!.amount, 0.001)
        assertEquals("lidl", result.description)
        assertNull(result.categoryId)
    }

    @Test
    fun testParseDecimalWithCommaAndKeywordGestern() {
        val result = SmartInputParser.parseInput("9,5 schuhe gestern")
        assertNotNull(result)
        assertEquals(-9.5, result!!.amount, 0.001)
        assertEquals("schuhe", result.description)
        assertNull(result.categoryId)

        val now = System.currentTimeMillis()
        val oneDayAgo = now - TimeUnit.DAYS.toMillis(1)
        // Check date is roughly 24 hours ago (within 5 seconds tolerance)
        assertTrue(kotlin.math.abs(oneDayAgo - result.dateMillis) < 5000)
    }

    @Test
    fun testParseLeadingDescriptionAndTrailingAmount() {
        val result = SmartInputParser.parseInput("tanken 60")
        assertNotNull(result)
        assertEquals(-60.0, result!!.amount, 0.001)
        assertEquals("tanken", result.description)
    }

    @Test
    fun testParseAmountOnlyFallbackDescription() {
        val result = SmartInputParser.parseInput("120")
        assertNotNull(result)
        assertEquals(-120.0, result!!.amount, 0.001)
        assertEquals("Ausgabe", result.description)
    }

    @Test
    fun testParseInvalidInputReturnsNull() {
        val result = SmartInputParser.parseInput("kein betrag hier")
        assertNull(result)
    }
}
