package com.budgettracker.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(
    userName: String,
    onUpdateUserName: (String) -> Unit,
    onClearAllData: () -> Unit,
    transactions: List<Transaction>,
    categories: List<CategoryBudget>,
    onAddCategory: (CategoryBudget) -> Unit = {},
    onUpdateCategory: (CategoryBudget) -> Unit = {},
    onDeleteCategory: (CategoryBudget) -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var nameInput by remember { mutableStateOf(userName) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Category Creation Dialog state
    var showAddCategoryModal by remember { mutableStateOf(false) }
    var newCatTitle by remember { mutableStateOf("") }
    var newCatLimit by remember { mutableStateOf("") }
    var newCatType by remember { mutableStateOf("AUSGABE") }
    var newCatColorHex by remember { mutableStateOf("#00E676") }

    // Category Edit state
    var editingCategory by remember { mutableStateOf<CategoryBudget?>(null) }
    var editLimitText by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN) }

    val presetColors = listOf("#00E676", "#FF5252", "#29B6F6", "#AB47BC", "#FFA726", "#26A69A", "#FF7043", "#8D6E63")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Einstellungen & Daten ⚙️", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: User Profile
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Benutzerprofil", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                onUpdateUserName(it)
                            },
                            label = { Text("Dein Name", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00E676),
                                unfocusedBorderColor = Color(0xFF444444)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Section 2: Category Management & Monthly Budget Limits
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Kategorien & Limits 🏷️", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Button(
                                onClick = { showAddCategoryModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("+ Neu", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        categories.forEach { category ->
                            val catColor = try { Color(android.graphics.Color.parseColor(category.colorHex)) } catch (e: Exception) { Color(0xFF00E676) }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(8.dp))
                                    .padding(10.dp),
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
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(category.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Limit: ${category.limit.toInt()} €", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            editingCategory = category
                                            editLimitText = category.limit.toInt().toString()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("✏️", fontSize = 13.sp)
                                    }
                                    IconButton(
                                        onClick = { onDeleteCategory(category) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("🗑", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 3: Backup & Export Options
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Backup & Export 💾", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Exportiere deine Daten als CSV / Backup für externe Sicherungen.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val csvContent = buildString {
                                        append("ID;Datum;Betrag;Kategorie;Beschreibung\n")
                                        transactions.forEach { tx ->
                                            val catName = categories.find { it.id == tx.categoryId }?.title ?: "Unzugeordnet"
                                            val dateStr = dateFormat.format(Date(tx.date))
                                            append("${tx.id};$dateStr;${tx.amount};$catName;\"${tx.description}\"\n")
                                        }
                                    }
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("BudgetTracker CSV", csvContent)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "CSV in Zwischenablage kopiert! 📋", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CSV Export 📄", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }


                // Section 5: Reset Data Danger Zone
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF331A1A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Daten verwalten", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Gesamte App zurücksetzen und alle Ausgaben & Kategorien löschen.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🗑 Alle Daten zurücksetzen", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Fertig", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )

    // Add New Category Dialog
    if (showAddCategoryModal) {
        AlertDialog(
            onDismissRequest = { showAddCategoryModal = false },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { Text("Neue Kategorie erstellen 🏷️", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCatTitle,
                        onValueChange = { newCatTitle = it },
                        label = { Text("Kategoriename (z. B. Freizeit)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = newCatLimit,
                        onValueChange = { newCatLimit = it },
                        label = { Text("Monatliches Limit (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    Text("Kategorie-Typ:", fontSize = 12.sp, color = Color.Gray)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = newCatType == "AUSGABE",
                            onClick = { newCatType = "AUSGABE" },
                            label = { Text("Ausgabe") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF00E676), selectedLabelColor = Color.Black)
                        )
                        FilterChip(
                            selected = newCatType == "RUECKLAGE",
                            onClick = { newCatType = "RUECKLAGE" },
                            label = { Text("Spar-Puffer") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF26A69A), selectedLabelColor = Color.White)
                        )
                    }

                    Text("Farbe wählen:", fontSize = 12.sp, color = Color.Gray)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        presetColors.take(5).forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .border(
                                        width = if (newCatColorHex == hex) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable { newCatColorHex = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limitVal = newCatLimit.toDoubleOrNull() ?: 0.0
                        if (newCatTitle.isNotBlank()) {
                            onAddCategory(
                                CategoryBudget(
                                    title = newCatTitle.trim(),
                                    limit = limitVal,
                                    type = newCatType,
                                    colorHex = newCatColorHex,
                                    position = categories.size
                                )
                            )
                            showAddCategoryModal = false
                            newCatTitle = ""
                            newCatLimit = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("Erstellen", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryModal = false }) {
                    Text("Abbrechen", color = Color.Gray)
                }
            }
        )
    }

    // Edit Category Limit Dialog
    editingCategory?.let { cat ->
        AlertDialog(
            onDismissRequest = { editingCategory = null },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { Text("${cat.title} limit anpassen", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editLimitText,
                    onValueChange = { editLimitText = it },
                    label = { Text("Neues Limit (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLim = editLimitText.toDoubleOrNull()
                        if (newLim != null && newLim >= 0) {
                            onUpdateCategory(cat.copy(limit = newLim))
                            editingCategory = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("Speichern", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Abbrechen", color = Color.Gray)
                }
            }
        )
    }

    // Confirmation Alert
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { Text("Wirklich alles löschen?", fontWeight = FontWeight.Bold) },
            text = { Text("Alle Buchungen und Kategorien werden unwiderruflich gelöscht. Die Einrichtung startet neu.") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onClearAllData()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Ja, unwiderruflich löschen", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Abbrechen", color = Color.Gray)
                }
            }
        )
    }
}
