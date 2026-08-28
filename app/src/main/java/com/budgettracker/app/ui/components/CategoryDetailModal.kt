package com.budgettracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CategoryDetailModal(
    category: CategoryBudget,
    transactions: List<Transaction>,
    onUpdateCategoryLimit: (newLimit: Double) -> Unit,
    onAddExpense: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    var limitInput by remember(category) { mutableStateOf(category.limit.toInt().toString()) }
    var isEditingLimit by remember { mutableStateOf(false) }

    val categoryTransactions = remember(transactions, category) {
        transactions.filter { it.categoryId == category.id }
    }

    val spentAmount = remember(categoryTransactions) {
        categoryTransactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
    }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E1E1E)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = category.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (category.type == "RUECKLAGE") "Spar-Puffer" else "Ausgabe",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color(0xFF2A2A2A), CircleShape)
                            .size(36.dp)
                    ) {
                        Text("✕", color = Color.White, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Budget & Spent Section
                Surface(
                    color = Color(0xFF262626),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Monatliches Limit", color = Color.Gray, fontSize = 13.sp)
                                if (!isEditingLimit) {
                                    Text(
                                        text = "${category.limit.toInt()} €",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (!isEditingLimit) {
                                TextButton(onClick = { isEditingLimit = true }) {
                                    Text("Limit ändern", color = Color(0xFF00E676), fontSize = 13.sp)
                                }
                            }
                        }

                        if (isEditingLimit) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = limitInput,
                                    onValueChange = { limitInput = it },
                                    label = { Text("Neues Limit (€)", color = Color.Gray) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = {
                                        val newLimit = limitInput.toDoubleOrNull()
                                        if (newLimit != null && newLimit >= 0) {
                                            onUpdateCategoryLimit(newLimit)
                                            isEditingLimit = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color(0xFF333333))
                        Spacer(modifier = Modifier.height(12.dp))

                        val isOverBudget = category.limit > 0 && spentAmount > category.limit

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bisher ausgegeben", color = Color.Gray, fontSize = 14.sp)
                            Text(
                                text = if (isOverBudget) "⚠️ ${String.format(Locale.GERMAN, "%.2f €", spentAmount)}" else String.format(Locale.GERMAN, "%.2f €", spentAmount),
                                color = if (isOverBudget) Color(0xFFFF5252) else Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button (Prominent)
                Button(
                    onClick = onAddExpense,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("➕ Ausgabe in dieser Kategorie", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Buchungen (${categoryTransactions.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Transactions List
                if (categoryTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Noch keine Buchungen.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categoryTransactions) { tx ->
                            Surface(
                                color = Color(0xFF262626),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tx.description,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = dateFormat.format(Date(tx.date)),
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = String.format(Locale.GERMAN, "%.2f €", tx.amount),
                                            color = if (tx.amount < 0) Color(0xFFFF5252) else Color(0xFF00E676),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        IconButton(
                                            onClick = { onEditTransaction(tx) },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(Color(0xFF333333), CircleShape)
                                        ) {
                                            Text("✏️", fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = { onDeleteTransaction(tx) },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(Color(0xFF333333), CircleShape)
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
}
