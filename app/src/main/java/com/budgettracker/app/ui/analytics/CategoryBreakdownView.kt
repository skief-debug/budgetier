package com.budgettracker.app.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.Transaction
import java.util.Locale

@Composable
fun CategoryBreakdownView(
    transactions: List<Transaction>,
    categories: List<CategoryBudget>,
    modifier: Modifier = Modifier
) {
    val totalExpenses = remember(transactions) {
        transactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
    }

    val categoryStats = remember(transactions, categories, totalExpenses) {
        categories.map { category ->
            val spent = transactions
                .filter { it.categoryId == category.id && it.amount < 0 }
                .sumOf { kotlin.math.abs(it.amount) }
            val percentage = if (totalExpenses > 0) (spent / totalExpenses) * 100 else 0.0
            val color = try {
                Color(android.graphics.Color.parseColor(category.colorHex))
            } catch (e: Exception) {
                Color(0xFF00E676)
            }
            CategoryStatItem(category, spent, percentage, color)
        }.sortedByDescending { it.spent }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Kategorien-Aufschlüsselung 📊",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Anteil der Ausgaben nach Kategorie",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (totalExpenses == 0.0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keine Ausgaben im aktuellen Zeitraum.", color = Color.Gray, fontSize = 13.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    categoryStats.forEach { stat ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(stat.color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stat.category.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = String.format(Locale.GERMAN, "%.2f € (%.1f%%)", stat.spent, stat.percentage),
                                    color = Color.LightGray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (stat.percentage / 100f).toFloat().coerceIn(0f, 1f) },
                                color = stat.color,
                                trackColor = Color(0xFF2A2A2A),
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

private data class CategoryStatItem(
    val category: CategoryBudget,
    val spent: Double,
    val percentage: Double,
    val color: Color
)
