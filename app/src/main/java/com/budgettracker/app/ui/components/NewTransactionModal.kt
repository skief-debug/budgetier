package com.budgettracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import java.util.Calendar

@Composable
fun NewTransactionModal(
    categories: List<CategoryBudget>,
    preselectedCategoryId: Int? = null,
    editingTransaction: Transaction? = null,
    onAddTransaction: (Transaction) -> Unit,
    onUpdateTransaction: ((Transaction) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.GERMAN) }

    var amountText by remember {
        mutableStateOf(
            editingTransaction?.let { kotlin.math.abs(it.amount).toString() } ?: ""
        )
    }
    var descriptionText by remember {
        mutableStateOf(editingTransaction?.description ?: "")
    }
    var dateText by remember { 
        mutableStateOf(
            editingTransaction?.let { dateFormat.format(java.util.Date(it.date)) }
                ?: dateFormat.format(java.util.Date())
        )
    }
    var selectedCategoryId by remember {
        mutableStateOf(editingTransaction?.categoryId ?: preselectedCategoryId ?: categories.firstOrNull()?.id)
    }
    var isExpense by remember {
        mutableStateOf(editingTransaction?.let { it.amount < 0 } ?: true)
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Text(
                text = if (editingTransaction != null) "Eintrag bearbeiten ✏️" else "Neuer Eintrag ✏️",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Type selector: Ausgabe vs Einnahme
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isExpense,
                        onClick = { isExpense = true },
                        label = { Text("Ausgabe (-)", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF5252),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF2A2A2A),
                            labelColor = Color.LightGray
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isExpense,
                        onClick = { isExpense = false },
                        label = { Text("Einnahme (+)", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00E676),
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF2A2A2A),
                            labelColor = Color.LightGray
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Betrag field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Betrag in €", color = Color.Gray) },
                    placeholder = { Text("z. B. 14,50", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Beschreibung field
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("Beschreibung / Laden", color = Color.Gray) },
                    placeholder = { Text("z. B. Supermarkt Lidl", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Datum field
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Datum (TT.MM.JJJJ)", color = Color.Gray) },
                    placeholder = { Text("z. B. 14.08.2026", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selection Chips
                Text("Kategorie wählen:", color = Color.LightGray, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.chunked(2).forEach { rowCats ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowCats.forEach { cat ->
                                val isSelected = selectedCategoryId == cat.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategoryId = cat.id },
                                    label = { Text(cat.title, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF00E676),
                                        selectedLabelColor = Color.Black,
                                        containerColor = Color(0xFF2A2A2A),
                                        labelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowCats.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                errorMessage?.let { msg ->
                    Text(text = msg, color = Color(0xFFFF5252), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rawVal = amountText.replace(',', '.').toDoubleOrNull()
                    if (rawVal != null && rawVal > 0) {
                        val finalAmount = if (isExpense) -rawVal else rawVal
                        val desc = descriptionText.ifBlank { if (isExpense) "Ausgabe" else "Einnahme" }
                        
                        // Parse date string to millis
                        val dateMillis = try {
                            val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.GERMAN)
                            val parsed = sdf.parse(dateText.trim())
                            parsed?.time ?: System.currentTimeMillis()
                        } catch (e: Exception) {
                            System.currentTimeMillis()
                        }

                        if (editingTransaction != null && onUpdateTransaction != null) {
                            onUpdateTransaction(
                                editingTransaction.copy(
                                    amount = finalAmount,
                                    description = desc,
                                    date = dateMillis,
                                    categoryId = selectedCategoryId
                                )
                            )
                        } else {
                            onAddTransaction(
                                Transaction(
                                    amount = finalAmount,
                                    description = desc,
                                    date = dateMillis,
                                    categoryId = selectedCategoryId
                                )
                            )
                        }
                        onDismiss()
                    } else {
                        errorMessage = "Bitte einen gültigen Betrag eingeben."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Speichern", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = Color.Gray)
            }
        }
    )
}
