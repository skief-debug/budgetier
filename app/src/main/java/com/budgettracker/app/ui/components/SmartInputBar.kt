package com.budgettracker.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.Transaction
import com.budgettracker.app.parser.SmartInputParser
import java.text.SimpleDateFormat
import java.util.*

/**
 * Smart Input Bar component for ultra-fast single-field transaction entries.
 * References spec.md Section 4
 */
@Composable
fun SmartInputBar(
    categories: List<CategoryBudget>,
    onAddTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fallback form state variables
    var manualAmount by remember { mutableStateOf("") }
    var manualDescription by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    fun submitSmartInput() {
        val parsed = SmartInputParser.parseInput(inputText)
        if (parsed != null) {
            onAddTransaction(
                Transaction(
                    amount = parsed.amount,
                    description = parsed.description,
                    date = parsed.dateMillis,
                    categoryId = parsed.categoryId
                )
            )
            inputText = ""
            errorMessage = null
        } else {
            errorMessage = "Bitte einen Betrag eingeben (z. B. '15 lidl' oder '9,50 tanken')"
        }
    }

    fun submitManualForm() {
        val amountVal = manualAmount.replace(',', '.').toDoubleOrNull()
        if (amountVal != null && amountVal > 0) {
            onAddTransaction(
                Transaction(
                    amount = -amountVal, // expenses negative
                    description = manualDescription.ifBlank { "Ausgabe" },
                    date = System.currentTimeMillis(),
                    categoryId = selectedCategoryId
                )
            )
            manualAmount = ""
            manualDescription = ""
            selectedCategoryId = null
            isExpanded = false
            errorMessage = null
        } else {
            errorMessage = "Ungültiger Betrag"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                    if (errorMessage != null) errorMessage = null
                },
                placeholder = {
                    Text(
                        text = "z. B. '15 lidl' oder '9,50 tanken'",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submitSmartInput() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    focusedBorderColor = Color(0xFF00E676),
                    unfocusedBorderColor = Color(0xFF444444),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { submitSmartInput() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(text = "OK", color = Color.Black, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = { isExpanded = !isExpanded }) {
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }

        errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = msg, color = Color(0xFFFF5252), fontSize = 12.sp)
        }

        // Manual Fallback Form (Expandable)
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                HorizontalDivider(color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Manuelle Eingabe (Fallback)",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = manualAmount,
                        onValueChange = { manualAmount = it },
                        label = { Text("Betrag (€)", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = manualDescription,
                        onValueChange = { manualDescription = it },
                        label = { Text("Beschreibung", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Selection Dropdown
                Text(text = "Kategorie wählen:", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = {
                                selectedCategoryId = if (selectedCategoryId == cat.id) null else cat.id
                            },
                            label = { Text(cat.title, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00E676),
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { submitManualForm() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Transaktion Speichern", color = Color.Black)
                }
            }
        }
    }
}
