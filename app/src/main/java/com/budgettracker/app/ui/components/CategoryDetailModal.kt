package com.budgettracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Surface(
                    color = Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (category.type == "RUECKLAGE") "Spar-Puffer" else "Ausgabe",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Budget Limit Card / Editor Section
                Surface(
                    color = Color(0xFF262626),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Monatliches Limit", color = Color.Gray, fontSize = 12.sp)
                                if (!isEditingLimit) {
                                    Text(
                                        text = "${category.limit.toInt()} €",
                                        color = Color(0xFF00E676),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (!isEditingLimit) {
                                OutlinedButton(
                                    onClick = { isEditingLimit = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Anpassen ✏️", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }

                        if (isEditingLimit) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = limitInput,
                                    onValueChange = { limitInput = it },
                                    label = { Text("Neues Limit (€)", color = Color.Gray, fontSize = 11.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val newLimit = limitInput.toDoubleOrNull()
                                        if (newLimit != null && newLimit >= 0) {
                                            onUpdateCategoryLimit(newLimit)
                                            isEditingLimit = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                                ) {
                                    Text("Speichern", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ausgegeben: ${String.format(Locale.GERMAN, "%.2f €", spentAmount)}",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section Header: Bookings + Direct Entry Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Einzelne Buchungen (${categoryTransactions.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Button(
                        onClick = onAddExpense,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("➕ Ausgabe hinzufügen", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (categoryTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Noch keine Buchungen in dieser Kategorie.", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categoryTransactions) { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2A2A2A), shape = RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.description,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = dateFormat.format(Date(tx.date)),
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format(Locale.GERMAN, "%.2f €", tx.amount),
                                        color = if (tx.amount < 0) Color(0xFFFF5252) else Color(0xFF00E676),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onEditTransaction(tx) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("✏️", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(2.dp))
                                    IconButton(
                                        onClick = { onDeleteTransaction(tx) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("🗑", fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
            ) {
                Text("Schließen", color = Color.Black)
            }
        }
    )
}
