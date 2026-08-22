package com.budgettracker.app.ui.onboarding

import androidx.compose.animation.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.R
import com.budgettracker.app.data.CategoryBudget

@Composable
fun OnboardingScreen(
    onCompleteOnboarding: (userName: String, selectedCategories: List<CategoryBudget>) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var nameInput by remember { mutableStateOf("") }
    
    // Default categories selection state
    val defaultCategories = remember {
        listOf(
            CategoryBudget(id = 1, title = "Essen & Trinken", limit = 450.0, type = "AUSGABE", position = 0, isExcluded = false, colorHex = "#00E676", iconName = "restaurant"),
            CategoryBudget(id = 2, title = "Auto & Transport", limit = 250.0, type = "AUSGABE", position = 1, isExcluded = false, colorHex = "#2196F3", iconName = "directions_car"),
            CategoryBudget(id = 3, title = "Freizeit & Hobby", limit = 200.0, type = "AUSGABE", position = 2, isExcluded = false, colorHex = "#AB47BC", iconName = "sports_esports"),
            CategoryBudget(id = 4, title = "Miete & Wohnen", limit = 850.0, type = "AUSGABE", position = 3, isExcluded = false, colorHex = "#FF9800", iconName = "home"),
            CategoryBudget(id = 5, title = "Spar-Puffer", limit = 300.0, type = "RUECKLAGE", position = 4, isExcluded = false, colorHex = "#26A69A", iconName = "savings")
        )
    }

    val selectedCategoryMap = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            defaultCategories.forEach { this[it.id] = true }
        }
    }

    val categoryLimitsMap = remember {
        mutableStateMapOf<Int, String>().apply {
            defaultCategories.forEach { this[it.id] = it.limit.toInt().toString() }
        }
    }

    var customCategoryName by remember { mutableStateOf("") }
    var customCategories by remember { mutableStateOf<List<CategoryBudget>>(emptyList()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo & Header
            Icon(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "BudgeTier Logo",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Willkommen bei BudgeTier! 🐊",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Dein smarter Begleiter für Finanzen & Budgets",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (step) {
                1 -> {
                    // Step 1: Name Input
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Schritt 1 von 3",
                                color = Color(0xFF00E676),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hey! Wie heißt du?",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                placeholder = { Text("z. B. Ansgar", color = Color.Gray) },
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
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { if (nameInput.isNotBlank()) step = 2 },
                        enabled = nameInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E676),
                            disabledContainerColor = Color(0xFF333333)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Weiter →", color = if (nameInput.isNotBlank()) Color.Black else Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                2 -> {
                    // Step 2: Category Picker
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Schritt 2 von 3",
                                color = Color(0xFF00E676),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Welche Kategorien möchtest du verwalten?",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            defaultCategories.forEach { cat ->
                                val isSelected = selectedCategoryMap[cat.id] ?: true
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Color(0xFF2A2A2A) else Color(0xFF181818))
                                        .clickable { selectedCategoryMap[cat.id] = !isSelected }
                                        .padding(12.dp)
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { selectedCategoryMap[cat.id] = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00E676))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = cat.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            // Custom Category Adding
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFF333333))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Eigene Kategorie hinzufügen:", color = Color.LightGray, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = customCategoryName,
                                    onValueChange = { customCategoryName = it },
                                    placeholder = { Text("z. B. Urlaub", color = Color.Gray, fontSize = 13.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF2A2A2A),
                                        unfocusedContainerColor = Color(0xFF2A2A2A),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (customCategoryName.isNotBlank()) {
                                            val newId = 100 + customCategories.size
                                            val newCat = CategoryBudget(
                                                id = newId,
                                                title = customCategoryName.trim(),
                                                limit = 200.0,
                                                type = "AUSGABE",
                                                position = defaultCategories.size + customCategories.size,
                                                isExcluded = false,
                                                colorHex = "#E91E63",
                                                iconName = "star"
                                            )
                                            customCategories = customCategories + newCat
                                            selectedCategoryMap[newId] = true
                                            categoryLimitsMap[newId] = "200"
                                            customCategoryName = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("+", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }

                            if (customCategories.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                customCategories.forEach { cat ->
                                    Text(text = "✓ ${cat.title}", color = Color(0xFF00E676), fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("Zurück", color = Color.White)
                        }
                        Button(
                            onClick = { step = 3 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("Weiter →", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                3 -> {
                    // Step 3: Budget Limits setup
                    val activeCategories = (defaultCategories + customCategories).filter { selectedCategoryMap[it.id] == true }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Schritt 3 von 3",
                                color = Color(0xFF00E676),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Setze deine monatlichen Budget-Limits (€):",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            activeCategories.forEach { cat ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text(text = cat.title, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                    OutlinedTextField(
                                        value = categoryLimitsMap[cat.id] ?: "0",
                                        onValueChange = { categoryLimitsMap[cat.id] = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF2A2A2A),
                                            unfocusedContainerColor = Color(0xFF2A2A2A),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(110.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { step = 2 },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("Zurück", color = Color.White)
                        }
                        Button(
                            onClick = {
                                val finalCategories = activeCategories.map { cat ->
                                    val limitVal = (categoryLimitsMap[cat.id] ?: "0").toDoubleOrNull() ?: cat.limit
                                    cat.copy(limit = limitVal)
                                }
                                onCompleteOnboarding(nameInput.trim(), finalCategories)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("Fertigstellen 🎉", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
