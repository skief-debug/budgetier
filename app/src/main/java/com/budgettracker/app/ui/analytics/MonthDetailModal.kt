package com.budgettracker.app.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.Transaction
import com.budgettracker.app.utils.DateUtils

@Composable
fun MonthDetailModal(
    monthOption: DateUtils.MonthOption,
    allTransactions: List<Transaction>,
    categories: List<CategoryBudget>,
    onDismiss: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    val monthTransactions = remember(allTransactions, monthOption) {
        allTransactions.filter {
            DateUtils.isDateInSpecificMonth(it.date, monthOption.year, monthOption.monthZeroBased)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            color = Color(0xFF121212)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = monthOption.label,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${monthTransactions.size} Transaktionen in diesem Monat",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Text(text = "✕", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Divider(color = Color(0xFF2A2A2A))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Category Breakdown
                    CategoryBreakdownView(
                        transactions = monthTransactions,
                        categories = categories
                    )

                    // 2. Smoothed Daily Expense Line Chart
                    SmoothedLineChart(
                        transactions = monthTransactions,
                        categories = categories
                    )

                    // 3. Calendar Heatmap
                    CalendarHeatmap(
                        transactions = monthTransactions
                    )

                    // 4. Detailed Transaction History for this month
                    TransactionHistoryView(
                        transactions = monthTransactions,
                        categories = categories,
                        onEditTransaction = { tx ->
                            onDismiss()
                            onEditTransaction(tx)
                        },
                        onDeleteTransaction = onDeleteTransaction
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
