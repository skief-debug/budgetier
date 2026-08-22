package com.budgettracker.app.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryView(
    transactions: List<Transaction>,
    categories: List<CategoryBudget>,
    onEditTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd. MMM yyyy", Locale.GERMAN) }

    val filteredTransactions = remember(transactions, searchQuery) {
        if (searchQuery.isBlank()) {
            transactions.sortedByDescending { it.date }
        } else {
            transactions.filter { tx ->
                val catName = categories.find { it.id == tx.categoryId }?.title ?: "Unzugeordnet"
                tx.description.contains(searchQuery, ignoreCase = true) ||
                        catName.contains(searchQuery, ignoreCase = true) ||
                        tx.amount.toString().contains(searchQuery)
            }.sortedByDescending { it.date }
        }
    }

    val totalSpentInList = remember(filteredTransactions) {
        filteredTransactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Verlauf Overview 📜",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredTransactions.size} Buchung(en) • ${String.format(Locale.GERMAN, "%.2f €", totalSpentInList)} Ausgaben",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Verlauf durchsuchen...", color = Color.Gray, fontSize = 13.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00E676),
                    unfocusedBorderColor = Color(0xFF333333),
                    focusedContainerColor = Color(0xFF121212),
                    unfocusedContainerColor = Color(0xFF121212)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keine Buchungen in diesem Zeitraum gefunden.", color = Color.Gray, fontSize = 13.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredTransactions.forEach { tx ->
                        val category = categories.find { it.id == tx.categoryId }
                        val catColorHex = category?.colorHex ?: "#757575"
                        val catColor = try { Color(android.graphics.Color.parseColor(catColorHex)) } catch (e: Exception) { Color.Gray }
                        val catTitle = category?.title ?: "Unzugeordnet"

                        Surface(
                            color = Color(0xFF262626),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(catColor)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (tx.description.isNotBlank()) tx.description else catTitle,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = catColor.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = catTitle,
                                                    color = catColor,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = dateFormat.format(Date(tx.date)),
                                                color = Color.Gray,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (tx.amount < 0) String.format(Locale.GERMAN, "-%.2f €", kotlin.math.abs(tx.amount)) else String.format(Locale.GERMAN, "+%.2f €", tx.amount),
                                        color = if (tx.amount < 0) Color(0xFFFF5252) else Color(0xFF00E676),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onEditTransaction(tx) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("✏️", fontSize = 12.sp)
                                    }
                                    IconButton(
                                        onClick = { onDeleteTransaction(tx) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("🗑", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
