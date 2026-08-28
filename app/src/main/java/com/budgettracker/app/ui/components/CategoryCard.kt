package com.budgettracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.CategoryBudget
import java.util.Calendar
import java.util.Locale

/**
 * Interactive Zettel-Optik Note Card component for category budgets.
 * References spec.md Section 3.1 & 3.2
 */
@Composable
fun CategoryCard(
    category: CategoryBudget,
    spentAmount: Double,
    unassignedCount: Int = 0,
    isWeeklyView: Boolean = false,
    onClick: () -> Unit,
    onUnassignedClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val limit = category.limit
    // Scale limit by 4 when in weekly view scope
    val effectiveLimit = if (isWeeklyView) limit / 4.0 else limit
    val remainingBudget = effectiveLimit - spentAmount

    // Calculate remaining days in active scope (weekly vs monthly)
    val calendar = Calendar.getInstance()
    val remainingDays = if (isWeeklyView) {
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        // Days remaining in week (Monday = 1 to Sunday = 7)
        val dayMonToSun = if (currentDayOfWeek == Calendar.SUNDAY) 7 else currentDayOfWeek - 1
        maxOf(1, 7 - dayMonToSun + 1)
    } else {
        val totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        maxOf(1, totalDaysInMonth - currentDayOfMonth + 1)
    }

    val dailyAllowance = if (remainingBudget > 0 && effectiveLimit > 0) {
        remainingBudget / remainingDays
    } else 0.0

    val rawRatio = if (effectiveLimit > 0) (spentAmount / effectiveLimit).toFloat() else 0f
    val progress = rawRatio.coerceIn(0f, 1f)

    val isOverBudget = effectiveLimit > 0 && spentAmount > effectiveLimit

    // Dynamic color: Green (<33%), Orange (33%-66%), Red (>66%)
    val progressColor = when {
        rawRatio > 0.66f -> Color(0xFFFF5252) // Red
        rawRatio > 0.33f -> Color(0xFFFF9800) // Orange
        else -> Color(0xFF00E676)             // Green
    }

    // Category accent color
    val cardAccentColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        Color(0xFF00E676)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isOverBudget) Color(0xFFFF5252).copy(alpha = 0.5f) else Color(0xFF2A2A2A),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Color Badge + Title + Type tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(cardAccentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = if (category.type == "RUECKLAGE") Color(0xFF26A69A).copy(alpha = 0.2f) else Color(0xFF333333),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (category.type == "RUECKLAGE") "Spar-Puffer" else "Ausgabe",
                        color = if (category.type == "RUECKLAGE") Color(0xFF26A69A) else Color.LightGray,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Display (Left: Remaining Budget) vs Secondary Display (Right: Budget Cap & Spent Amount)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side (Primary, Largest & Most Prominent): Remaining / Available Budget
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Verbleibendes Budget",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (effectiveLimit <= 0) {
                            "Kein Limit"
                        } else if (isOverBudget) {
                            "⚠️ ${String.format(Locale.GERMAN, "%.2f €", spentAmount - effectiveLimit)} überzogen"
                        } else {
                            "${String.format(Locale.GERMAN, "%.2f €", remainingBudget)} übrig"
                        },
                        color = if (isOverBudget) Color(0xFFFF5252) else Color(0xFF00E676),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (effectiveLimit > 0 && !isOverBudget) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${String.format(Locale.GERMAN, "%.2f €", dailyAllowance)} / Tag ($remainingDays T.)",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                // Right Side (Secondary Display, Compact/Subtle): Total Budget & Spent Amount
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (effectiveLimit > 0) "von ${effectiveLimit.toInt()} € Budget" else "Ohne Limit",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${String.format(Locale.GERMAN, "%.2f €", spentAmount)} ausgegeben",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            if (effectiveLimit > 0) {
                LinearProgressIndicator(
                    progress = { progress },
                    color = progressColor,
                    trackColor = Color(0xFF2A2A2A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }

            // Unassigned Transactions Pulsing Banner (if any)
            if (unassignedCount > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFF5252).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUnassignedClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠ $unassignedCount unzugeordnete Buchung(en) – Antippen zum Sortieren",
                            color = Color(0xFFFF5252),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
