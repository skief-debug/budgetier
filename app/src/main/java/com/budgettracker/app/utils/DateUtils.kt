package com.budgettracker.app.utils

import java.util.Calendar

object DateUtils {

    fun getStartOfWeekMillis(): Long {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (cal.timeInMillis > System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, -7)
        }
        return cal.timeInMillis
    }

    fun getEndOfWeekMillis(): Long {
        val startOfWeek = getStartOfWeekMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = startOfWeek
        cal.add(Calendar.DAY_OF_YEAR, 6)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun getStartOfMonthMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfMonthMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun isDateInWeek(dateMillis: Long): Boolean {
        return dateMillis in getStartOfWeekMillis()..getEndOfWeekMillis()
    }

    fun isDateInMonth(dateMillis: Long): Boolean {
        return dateMillis in getStartOfMonthMillis()..getEndOfMonthMillis()
    }

    data class MonthOption(
        val year: Int,
        val monthZeroBased: Int,
        val label: String
    )

    private val monthNames = arrayOf(
        "Januar", "Februar", "März", "April", "Mai", "Juni",
        "Juli", "August", "September", "Oktober", "November", "Dezember"
    )

    fun getMonthName(monthZeroBased: Int): String {
        return if (monthZeroBased in 0..11) monthNames[monthZeroBased] else ""
    }

    fun isDateInSpecificMonth(dateMillis: Long, year: Int, monthZeroBased: Int): Boolean {
        val cal = Calendar.getInstance()
        cal.timeInMillis = dateMillis
        return cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == monthZeroBased
    }

    fun getAvailableMonths(transactions: List<com.budgettracker.app.data.Transaction>): List<MonthOption> {
        val monthSet = mutableSetOf<Pair<Int, Int>>()

        val currentCal = Calendar.getInstance()
        val currentYear = currentCal.get(Calendar.YEAR)
        val currentMonth = currentCal.get(Calendar.MONTH)
        monthSet.add(Pair(currentYear, currentMonth))

        // Add ONLY months that actually have recorded transactions
        transactions.forEach { tx ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = tx.date
            monthSet.add(Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)))
        }

        return monthSet.sortedWith(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })
            .map { (y, m) ->
                MonthOption(
                    year = y,
                    monthZeroBased = m,
                    label = "${monthNames[m]} $y"
                )
            }
    }
}


