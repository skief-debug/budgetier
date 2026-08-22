package com.budgettracker.app.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.Transaction
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class DailyExpenseDataPoint(
    val dateMillis: Long,
    val dayLabel: String,
    val amount: Double
)

/**
 * Smoothed Cubic-Bezier Line Chart (Canvas-drawn) with category filter tags & interactive scrubbing indicator.
 * References spec.md Section 5.1
 */
@Composable
fun SmoothedLineChart(
    transactions: List<Transaction>,
    categories: List<CategoryBudget>,
    modifier: Modifier = Modifier
) {
    // Active category filters (All selected by default)
    var selectedCategoryIds by remember(categories) {
        mutableStateOf(categories.map { it.id }.toSet())
    }

    // Scrubbing state
    var touchX by remember { mutableStateOf<Float?>(null) }
    var selectedDataPoint by remember { mutableStateOf<DailyExpenseDataPoint?>(null) }

    // Aggregate transactions per day over the last 30 days
    val dailyDataPoints = remember(transactions, selectedCategoryIds) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayMidnight = cal.timeInMillis
        val dayMillis = TimeUnit.DAYS.toMillis(1)
        val dateFormat = SimpleDateFormat("dd.MM", Locale.GERMAN)

        val points = mutableListOf<DailyExpenseDataPoint>()

        for (i in 29 downTo 0) {
            val dayStart = todayMidnight - (i * dayMillis)
            val dayEnd = dayStart + dayMillis

            val dayTotal = transactions
                .filter { it.date in dayStart until dayEnd }
                .filter { it.categoryId == null || selectedCategoryIds.contains(it.categoryId) }
                .filter { it.amount < 0 } // Only expenses
                .sumOf { kotlin.math.abs(it.amount) }

            points.add(
                DailyExpenseDataPoint(
                    dateMillis = dayStart,
                    dayLabel = dateFormat.format(Date(dayStart)),
                    amount = dayTotal
                )
            )
        }
        points
    }

    val maxAmount = remember(dailyDataPoints) {
        maxOf(100.0, dailyDataPoints.maxOfOrNull { it.amount } ?: 100.0)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "30-Tage Ausgaben-Verlauf",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Smoothed Line Chart (Wische über die Kurve)",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tooltip when scrubbing
            selectedDataPoint?.let { dp ->
                Surface(
                    color = Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Datum: ${dp.dayLabel}", color = Color.LightGray, fontSize = 12.sp)
                        Text(
                            text = String.format(Locale.GERMAN, "Ausgaben: %.2f €", dp.amount),
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Canvas Line Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                touchX = offset.x
                            },
                            onDrag = { change, _ ->
                                touchX = change.position.x
                            },
                            onDragEnd = {
                                touchX = null
                                selectedDataPoint = null
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (dailyDataPoints.isEmpty()) return@Canvas

                    val width = size.width
                    val height = size.height
                    val stepX = width / (dailyDataPoints.size - 1)

                    val strokePath = Path()
                    val fillPath = Path()

                    val points = dailyDataPoints.mapIndexed { index, dp ->
                        val x = index * stepX
                        val y = height - ((dp.amount / maxAmount) * height * 0.85f).toFloat()
                        Offset(x, y)
                    }

                    // Build Cubic-Bezier smooth path
                    if (points.isNotEmpty()) {
                        strokePath.moveTo(points[0].x, points[0].y)
                        fillPath.moveTo(points[0].x, height)
                        fillPath.lineTo(points[0].x, points[0].y)

                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2f, p1.y)
                            val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2f, p2.y)

                            strokePath.cubicTo(
                                controlPoint1.x, controlPoint1.y,
                                controlPoint2.x, controlPoint2.y,
                                p2.x, p2.y
                            )
                            fillPath.cubicTo(
                                controlPoint1.x, controlPoint1.y,
                                controlPoint2.x, controlPoint2.y,
                                p2.x, p2.y
                            )
                        }

                        fillPath.lineTo(points.last().x, height)
                        fillPath.close()

                        // Draw Gradient Fill beneath curve
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF00E676).copy(alpha = 0.3f), Color.Transparent)
                            )
                        )

                        // Draw Bezier Stroke Line
                        drawPath(
                            path = strokePath,
                            color = Color(0xFF00E676),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Handle Dragging Scrub Line
                        touchX?.let { tx ->
                            val clampedX = tx.coerceIn(0f, width)
                            val selectedIndex = (clampedX / stepX).toInt().coerceIn(0, dailyDataPoints.size - 1)
                            val targetPoint = points[selectedIndex]
                            selectedDataPoint = dailyDataPoints[selectedIndex]

                            // Vertical Scrub Bar Line
                            drawLine(
                                color = Color.White.copy(alpha = 0.6f),
                                start = Offset(targetPoint.x, 0f),
                                end = Offset(targetPoint.x, height),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Highlight Circle Dot
                            drawCircle(
                                color = Color(0xFF00E676),
                                radius = 6.dp.toPx(),
                                center = targetPoint
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.dp.toPx(),
                                center = targetPoint
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Tag Chips Filter (Toggle Categories On/Off)
            Text("Kategorien-Filter:", color = Color.Gray, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategoryIds.contains(cat.id)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryIds = if (isSelected) {
                                selectedCategoryIds - cat.id
                            } else {
                                selectedCategoryIds + cat.id
                            }
                        },
                        label = { Text(cat.title, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00E676),
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF2A2A2A),
                            labelColor = Color.LightGray
                        )
                    )
                }
            }
        }
    }
}
