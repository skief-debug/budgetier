package com.budgettracker.app.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.Transaction
import com.budgettracker.app.utils.DateUtils
import java.util.*

@Composable
fun MonthlyComparisonView(
    allTransactions: List<Transaction>,
    categories: List<CategoryBudget>,
    onMonthClick: (DateUtils.MonthOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthOptions = remember(allTransactions) {
        DateUtils.getAvailableMonths(allTransactions)
    }

    val totalMonthlyLimit = remember(categories) {
        categories.filter { !it.isExcluded && it.type == "AUSGABE" }.sumOf { it.limit }
    }

    val monthlyStats = remember(allTransactions, monthOptions, totalMonthlyLimit) {
        monthOptions.map { option ->
            val spent = allTransactions
                .filter { DateUtils.isDateInSpecificMonth(it.date, option.year, option.monthZeroBased) && it.amount < 0 }
                .sumOf { kotlin.math.abs(it.amount) }
            val isOverBudget = totalMonthlyLimit > 0 && spent > totalMonthlyLimit
            MonthlyStatItem(
                option = option,
                label = option.label,
                spent = spent,
                limit = totalMonthlyLimit,
                isOverBudget = isOverBudget
            )
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Rückblickende Budgets 📊",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Klicke auf einen Monat, um die detaillierte Auswertung & Statistiken zu öffnen",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                monthlyStats.forEach { stat ->
                    Surface(
                        color = Color(0xFF262626),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMonthClick(stat.option) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = stat.label,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "➔",
                                        color = Color(0xFF00E676),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    color = if (stat.isOverBudget) Color(0xFFFF5252).copy(alpha = 0.2f) else Color(0xFF00E676).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (stat.isOverBudget) "⚠ Über Budget" else "✓ Im Budget",
                                        color = if (stat.isOverBudget) Color(0xFFFF5252) else Color(0xFF00E676),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(Locale.GERMAN, "Ausgaben: %.2f €", stat.spent),
                                    color = if (stat.isOverBudget) Color(0xFFFF5252) else Color.LightGray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = String.format(Locale.GERMAN, "von %.0f € Budget", stat.limit),
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val progress = if (stat.limit > 0) (stat.spent / stat.limit).toFloat().coerceIn(0f, 1f) else 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                color = if (stat.isOverBudget) Color(0xFFFF5252) else Color(0xFF00E676),
                                trackColor = Color(0xFF1A1A1A),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class MonthlyStatItem(
    val option: DateUtils.MonthOption,
    val label: String,
    val spent: Double,
    val limit: Double,
    val isOverBudget: Boolean
)
