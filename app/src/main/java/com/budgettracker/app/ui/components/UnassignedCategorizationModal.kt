package com.budgettracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

/**
 * Modal dialog for assigning unassigned transactions to category budget note cards with 1 tap.
 * References spec.md Section 4.1 Step 4
 */
@Composable
fun UnassignedCategorizationModal(
    unassignedTransactions: List<Transaction>,
    categories: List<CategoryBudget>,
    onAssignCategory: (transactionId: Int, categoryId: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        title = {
            Text(
                text = "Unzugeordnete Buchungen (${unassignedTransactions.size})",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tippe auf eine Buchung, um sie einer Kategorie zuzuweisen:",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTransaction == null) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        items(unassignedTransactions) { tx ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTransaction = tx }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
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
                                    Text(
                                        text = String.format(Locale.GERMAN, "%.2f €", tx.amount),
                                        color = Color(0xFFFF5252),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val tx = selectedTransaction!!
                    Text(
                        text = "Kategorie wählen für '${tx.description}' (${String.format(Locale.GERMAN, "%.2f €", tx.amount)}):",
                        color = Color(0xFF00E676),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        items(categories) { cat ->
                            Button(
                                onClick = {
                                    onAssignCategory(tx.id, cat.id)
                                    selectedTransaction = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cat.title, color = Color.White)
                                    Text(
                                        text = if (cat.limit > 0) "${cat.limit.toInt()} €" else "Kein Limit",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { selectedTransaction = null }) {
                        Text("Zurück zur Liste", color = Color.LightGray)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Schließen", color = Color(0xFF00E676))
            }
        }
    )
}
