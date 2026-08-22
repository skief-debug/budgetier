package com.budgettracker.app.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CalendarDayHeatmapData(
    val dayNumber: Int,
    val totalExpense: Double,
    val isCurrentMonth: Boolean
)

/**
 * Monthly Calendar Heatmap component displaying expense intensity per day.
 * References spec.md Section 5.2
 */
@Composable
fun CalendarHeatmap(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    val monthName = remember {
        SimpleDateFormat("MMMM yyyy", Locale.GERMAN).format(calendar.time)
    }

    val daysData = remember(transactions) {
        val list = mutableListOf<CalendarDayHeatmapData>()
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        // Monday = 1 ... Sunday = 7
        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7

        // Leading empty offset days
        for (i in 0 until firstDayOfWeek) {
            list.add(CalendarDayHeatmapData(0, 0.0, false))
        }

        // Days of month
        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val dayStart = cal.timeInMillis
            val dayEnd = dayStart + (24 * 3600 * 1000L)

            val totalSpent = transactions
                .filter { it.date in dayStart until dayEnd }
                .filter { it.amount < 0 }
                .sumOf { kotlin.math.abs(it.amount) }

            list.add(CalendarDayHeatmapData(day, totalSpent, true))
        }
        list
    }

    val weekHeader = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Kalender Heatmap",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = monthName,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Weekday Header (Mo - So)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekHeader.forEach { day ->
                    Text(
                        text = day,
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.width(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid Rows (7 columns)
            val rows = daysData.chunked(7)
            rows.forEach { rowDays ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until 7) {
                        val dayData = rowDays.getOrNull(i)
                        if (dayData != null && dayData.isCurrentMonth) {
                            val expense = dayData.totalExpense

                            // Color Mapping according to spec.md Section 5.2
                            val (bgColor, textColor) = when {
                                expense <= 0.0 -> Pair(Color(0xFF2A2A2A), Color.Gray)
                                expense < 15.0 -> Pair(Color(0xFF1B382B), Color(0xFF00E676))
                                expense in 15.0..50.0 -> Pair(Color(0xFF6B4A00), Color(0xFFFFB74D))
                                else -> Pair(Color(0xFF661717), Color(0xFFFF5252))
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .border(
                                        width = 1.dp,
                                        color = if (expense > 0) bgColor else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${dayData.dayNumber}",
                                        color = textColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            // Blank cell offset
                            Box(modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color(0xFF2A2A2A), label = "0 €")
                LegendItem(color = Color(0xFF1B382B), label = "< 15 €")
                LegendItem(color = Color(0xFF6B4A00), label = "15-50 €")
                LegendItem(color = Color(0xFF661717), label = "> 50 €")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = Color.Gray, fontSize = 10.sp)
    }
}
