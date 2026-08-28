package com.budgettracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHeader(
    totalSpent: Double,
    totalLimit: Double,
    isWeeklyView: Boolean,
    onToggleViewScope: (isWeekly: Boolean) -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Effective budget limit scaled by 4 if in weekly view
    val effectiveLimit = if (isWeeklyView) totalLimit / 4.0 else totalLimit
    val remaining = effectiveLimit - totalSpent

    // Calculate remaining days in active scope (weekly vs monthly)
    val calendar = java.util.Calendar.getInstance()
    val remainingDays = if (isWeeklyView) {
        val currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val dayMonToSun = if (currentDayOfWeek == java.util.Calendar.SUNDAY) 7 else currentDayOfWeek - 1
        maxOf(1, 7 - dayMonToSun + 1)
    } else {
        val totalDaysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val currentDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        maxOf(1, totalDaysInMonth - currentDayOfMonth + 1)
    }

    val dailyAllowance = if (remaining > 0 && effectiveLimit > 0) {
        remaining / remainingDays
    } else 0.0

    val rawRatio = if (effectiveLimit > 0) (totalSpent / effectiveLimit).toFloat() else 0f
    val progress = rawRatio.coerceIn(0f, 1f)
    val isOver = effectiveLimit > 0 && totalSpent > effectiveLimit

    // Dynamic progress bar colors: Green (<70%), Orange (70%-99%), Red (>=100%)
    val statusColor = when {
        isOver || rawRatio >= 1.0f -> Color(0xFFFF5252) // Red
        rawRatio >= 0.70f -> Color(0xFFFF9800)          // Orange
        else -> Color(0xFF00E676)                       // Green
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top Row: App Logo + Welcome & Settings Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF2A2A2A),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "BudgeTier Logo",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Dashboard",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isWeeklyView) "Wöchentliche Ansicht" else "Monatliche Ansicht",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenSettings) {
                        Text("⚙️", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scope Toggle Switch (Monat / Woche)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = !isWeeklyView,
                        onClick = { onToggleViewScope(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Color(0xFF00E676),
                            activeContentColor = Color.Black,
                            inactiveContainerColor = Color(0xFF2A2A2A),
                            inactiveContentColor = Color.White
                        )
                    ) {
                        Text("Monatlich", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    SegmentedButton(
                        selected = isWeeklyView,
                        onClick = { onToggleViewScope(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Color(0xFF00E676),
                            activeContentColor = Color.Black,
                            inactiveContainerColor = Color(0xFF2A2A2A),
                            inactiveContentColor = Color.White
                        )
                    ) {
                        Text("Wöchentlich", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Display (Left: Remaining Budget & Daily Allowance) vs Secondary Display (Right: Budget Cap & Spent Amount)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side (Primary, Prominent): Remaining / Available Budget
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isWeeklyView) "Verbleibend diese Woche" else "Verbleibendes Budget",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (effectiveLimit <= 0) {
                            "Kein Limit"
                        } else if (isOver) {
                            "${String.format(Locale.GERMAN, "%.2f €", totalSpent - effectiveLimit)} überzogen"
                        } else {
                            "${String.format(Locale.GERMAN, "%.2f €", remaining)} übrig"
                        },
                        color = if (isOver) Color(0xFFFF5252) else Color(0xFF00E676),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (effectiveLimit > 0 && !isOver) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${String.format(Locale.GERMAN, "%.2f €", dailyAllowance)} / Tag ($remainingDays T.)",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                // Right Side (Secondary Display, Compact): Total Budget & Spent Amount
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isWeeklyView) "von ${effectiveLimit.toInt()} € / Woche" else "von ${effectiveLimit.toInt()} € / Monat",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${String.format(Locale.GERMAN, "%.2f €", totalSpent)} ausgegeben",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Global Progress Bar with dynamic statusColor
            LinearProgressIndicator(
                progress = { progress },
                color = statusColor,
                trackColor = Color(0xFF2A2A2A),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}
