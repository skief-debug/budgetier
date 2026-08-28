package com.budgettracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.data.CategoryBudget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onClearAllData: () -> Unit,
    categories: List<CategoryBudget>,
    onAddCategory: (CategoryBudget) -> Unit = {},
    onUpdateCategory: (CategoryBudget) -> Unit = {},
    onDeleteCategory: (CategoryBudget) -> Unit = {},
    onDismiss: () -> Unit
) {
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

    val presetColors = listOf("#00E676", "#FF5252", "#29B6F6", "#AB47BC", "#FFA726", "#26A69A", "#FF7043", "#8D6E63")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Einstellungen & Daten", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section 1: Category Management & Monthly Budget Limits
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Kategorien & Limits", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Button(
                            onClick = { showAddCategoryModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Neu", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    categories.forEachIndexed { index, category ->
                        val catColor = try { Color(android.graphics.Color.parseColor(category.colorHex)) } catch (e: Exception) { Color(0xFF00E676) }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(catColor)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(category.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Limit: ${category.limit.toInt()} €", color = Color.Gray, fontSize = 13.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        editingCategory = category
                                        editLimitText = category.limit.toInt().toString()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text("✏️", fontSize = 16.sp)
                                }
                                IconButton(
                                    onClick = { onDeleteCategory(category) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text("🗑", fontSize = 16.sp)
                                }
                            }
                        }
                        if (index < categories.size - 1) {
                            HorizontalDivider(color = Color(0xFF2A2A2A), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }

            // Section 2: Reset Data Danger Zone
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Gefahrenzone", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Hiermit setzt du die gesamte App zurück. Alle Ausgaben und Kategorien werden gelöscht.",
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFF5252), RoundedCornerShape(12.dp))
                    ) {
                        Text("App zurücksetzen", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Add New Category Dialog
    if (showAddCategoryModal) {
        AlertDialog(
            onDismissRequest = { showAddCategoryModal = false },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { Text("Neue Kategorie", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = newCatTitle,
                        onValueChange = { newCatTitle = it },
                        label = { Text("Name", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, 
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color(0xFF444444)
                        )
                    )
                    OutlinedTextField(
                        value = newCatLimit,
                        onValueChange = { newCatLimit = it },
                        label = { Text("Limit (€)", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, 
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color(0xFF444444)
                        )
                    )

                    Column {
                        Text("Typ:", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    }

                    Column {
                        Text("Farbe:", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            presetColors.take(6).forEach { hex ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(hex)))
                                        .border(
                                            width = if (newCatColorHex == hex) 3.dp else 0.dp,
                                            color = if (newCatColorHex == hex) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { newCatColorHex = hex }
                                )
                            }
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
                    Text("Speichern", color = Color.Black, fontWeight = FontWeight.Bold)
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
            title = { Text("${cat.title} Limit", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editLimitText,
                    onValueChange = { editLimitText = it },
                    label = { Text("Neues Limit (€)", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, 
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color(0xFF444444)
                    )
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
                    Text("Speichern", color = Color.Black, fontWeight = FontWeight.Bold)
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
            title = { Text("App zurücksetzen?", fontWeight = FontWeight.Bold) },
            text = { Text("Dieser Schritt kann nicht rückgängig gemacht werden. Alle Daten gehen verloren.") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onClearAllData()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Ja, löschen", color = Color.White, fontWeight = FontWeight.Bold)
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
