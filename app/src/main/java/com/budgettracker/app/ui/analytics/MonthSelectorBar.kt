package com.budgettracker.app.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.utils.DateUtils

@Composable
fun MonthSelectorBar(
    availableMonths: List<DateUtils.MonthOption>,
    selectedMonth: DateUtils.MonthOption?,
    onSelectMonth: (DateUtils.MonthOption?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currentIndex = availableMonths.indexOf(selectedMonth)

    Surface(
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Month Button
            IconButton(
                onClick = {
                    if (currentIndex < availableMonths.size - 1) {
                        onSelectMonth(availableMonths[currentIndex + 1])
                    }
                },
                enabled = currentIndex < availableMonths.size - 1,
                modifier = Modifier.size(36.dp)
            ) {
                Text(
                    text = "◀",
                    color = if (currentIndex < availableMonths.size - 1) Color(0xFF00E676) else Color.DarkGray,
                    fontSize = 14.sp
                )
            }

            // Month Dropdown Button
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    color = Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = selectedMonth?.label ?: "Alle Buchungen",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("▼", color = Color(0xFF00E676), fontSize = 12.sp)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF2A2A2A))
                ) {
                    DropdownMenuItem(
                        text = { Text("Alle Buchungen (Gesamt)", color = if (selectedMonth == null) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold) },
                        onClick = {
                            onSelectMonth(null)
                            expanded = false
                        }
                    )
                    HorizontalDivider(color = Color(0xFF333333))
                    availableMonths.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.label,
                                    color = if (selectedMonth == option) Color(0xFF00E676) else Color.White,
                                    fontWeight = if (selectedMonth == option) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSelectMonth(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Next Month Button
            IconButton(
                onClick = {
                    if (currentIndex > 0) {
                        onSelectMonth(availableMonths[currentIndex - 1])
                    }
                },
                enabled = currentIndex > 0,
                modifier = Modifier.size(36.dp)
            ) {
                Text(
                    text = "▶",
                    color = if (currentIndex > 0) Color(0xFF00E676) else Color.DarkGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
