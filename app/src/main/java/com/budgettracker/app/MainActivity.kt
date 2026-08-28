package com.budgettracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgettracker.app.BuildConfig
import com.budgettracker.app.data.AppDatabase
import com.budgettracker.app.data.CategoryBudget
import com.budgettracker.app.data.MockDataGenerator
import com.budgettracker.app.data.PreferenceManager
import com.budgettracker.app.data.Transaction
import com.budgettracker.app.ui.analytics.CalendarHeatmap
import com.budgettracker.app.ui.analytics.CategoryBreakdownView
import com.budgettracker.app.ui.analytics.MonthDetailModal
import com.budgettracker.app.ui.analytics.MonthlyComparisonView
import com.budgettracker.app.ui.analytics.SmoothedLineChart
import com.budgettracker.app.ui.analytics.TransactionHistoryView
import com.budgettracker.app.ui.components.*
import com.budgettracker.app.ui.onboarding.OnboardingScreen
import com.budgettracker.app.utils.DateUtils
import com.budgettracker.app.utils.UpdateCheckResult
import com.budgettracker.app.utils.UpdateInfo
import com.budgettracker.app.utils.UpdateManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF121212)
            ) {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val prefManager = remember { PreferenceManager(context) }

                var isOnboardingCompleted by remember { mutableStateOf(prefManager.isOnboardingCompleted) }

                // ── Auto-Updater ──────────────────────────────────────
                var pendingUpdate by remember { mutableStateOf<UpdateInfo?>(null) }
                LaunchedEffect(Unit) {
                    launch {
                        val result = UpdateManager.checkForUpdate(BuildConfig.VERSION_CODE)
                        if (result is UpdateCheckResult.UpdateAvailable) {
                            pendingUpdate = result.info
                        }
                    }
                }
                // ──────────────────────────────────────────────────────

                // Database reference
                val db = remember { AppDatabase.getDatabase(context) }

                // State holders
                var categories by remember { mutableStateOf<List<CategoryBudget>>(emptyList()) }
                var allTransactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
                var isWeeklyView by remember { mutableStateOf(false) }
                var selectedTab by remember { mutableStateOf(0) } // 0 = Dashboard, 1 = Analytics

                // Modals state
                var showNewTransactionModal by remember { mutableStateOf(false) }
                var preselectedCategoryIdForNewTx by remember { mutableStateOf<Int?>(null) }
                var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
                var selectedCategoryForDetail by remember { mutableStateOf<CategoryBudget?>(null) }
                var showUnassignedModal by remember { mutableStateOf(false) }
                var showSettingsModal by remember { mutableStateOf(false) }

                // Observe Database Flows
                LaunchedEffect(isOnboardingCompleted) {
                    if (isOnboardingCompleted) {
                        launch {
                            db.categoryBudgetDao().getAllCategoriesFlow().collect { list ->
                                categories = list
                            }
                        }

                        launch {
                            db.transactionDao().getAllTransactionsFlow().collect { list ->
                                allTransactions = list
                            }
                        }
                    }
                }

                if (!isOnboardingCompleted) {
                    // Onboarding Flow
                    OnboardingScreen(
                        onCompleteOnboarding = { newCategories ->
                            scope.launch {
                                db.categoryBudgetDao().deleteAllCategories()
                                db.categoryBudgetDao().insertCategories(newCategories)
                                prefManager.isOnboardingCompleted = true
                                isOnboardingCompleted = true
                            }
                        }
                    )
                } else {
                    var selectedMonthForDetail by remember { mutableStateOf<DateUtils.MonthOption?>(null) }

                    // Filter transactions by selected timeframe scope (Weekly vs Monthly)
                    val activeScopedTransactions = remember(allTransactions, isWeeklyView) {
                        if (isWeeklyView) {
                            allTransactions.filter { DateUtils.isDateInWeek(it.date) }
                        } else {
                            allTransactions.filter { DateUtils.isDateInMonth(it.date) }
                        }
                    }

                    val unassignedTransactions = remember(allTransactions) {
                        allTransactions.filter { it.categoryId == null }
                    }

                    val totalSpent = remember(activeScopedTransactions) {
                        activeScopedTransactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
                    }

                    val totalLimit = remember(categories) {
                        categories.filter { !it.isExcluded && it.type == "AUSGABE" }.sumOf { it.limit }
                    }

                    Scaffold(
                        floatingActionButton = {
                            if (selectedTab == 0) {
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        preselectedCategoryIdForNewTx = null
                                        editingTransaction = null
                                        showNewTransactionModal = true
                                    },
                                    containerColor = Color(0xFF00E676),
                                    contentColor = Color.Black,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("➕", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ausgabe", fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color(0xFF1E1E1E),
                                contentColor = Color.White
                            ) {
                                NavigationBarItem(
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 },
                                    icon = { Text("📊", fontSize = 18.sp) },
                                    label = { Text("Dashboard", color = if (selectedTab == 0) Color(0xFF00E676) else Color.Gray) },
                                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF2A2A2A))
                                )
                                NavigationBarItem(
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 },
                                    icon = { Text("📈", fontSize = 18.sp) },
                                    label = { Text("Statistiken", color = if (selectedTab == 1) Color(0xFF00E676) else Color.Gray) },
                                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF2A2A2A))
                                )
                            }
                        },
                        containerColor = Color(0xFF121212)
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (selectedTab == 0) {
                                // Dashboard Tab
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Header Module
                                    DashboardHeader(
                                        totalSpent = totalSpent,
                                        totalLimit = totalLimit,
                                        isWeeklyView = isWeeklyView,
                                        onToggleViewScope = { isWeeklyView = it },
                                        onOpenSettings = { showSettingsModal = true }
                                    )

                                    // Unassigned Banner Prompt
                                    if (unassignedTransactions.isNotEmpty()) {
                                        Surface(
                                            color = Color(0xFFFF5252).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "⚠ ${unassignedTransactions.size} unzugeordnete Buchung(en)",
                                                    color = Color(0xFFFF5252),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Button(
                                                    onClick = { showUnassignedModal = true },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text("Jetzt zuordnen", color = Color.White, fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Kategorien",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Category Cards List
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        categories.forEach { category ->
                                            val spentForCat = activeScopedTransactions
                                                .filter { it.categoryId == category.id && it.amount < 0 }
                                                .sumOf { kotlin.math.abs(it.amount) }

                                            CategoryCard(
                                                category = category,
                                                spentAmount = spentForCat,
                                                unassignedCount = if (category.id == categories.firstOrNull()?.id) unassignedTransactions.size else 0,
                                                isWeeklyView = isWeeklyView,
                                                onClick = {
                                                    selectedCategoryForDetail = category
                                                },
                                                onUnassignedClick = {
                                                    showUnassignedModal = true
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(70.dp))
                                }
                            } else {
                                // Statistiken Tab
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "Statistiken 📊",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Rückblickende Budgets (Overview of recorded months, clickable)
                                    MonthlyComparisonView(
                                        allTransactions = allTransactions,
                                        categories = categories,
                                        onMonthClick = { month ->
                                            selectedMonthForDetail = month
                                        }
                                    )

                                    // Global Transaction History with Search & Action Buttons
                                    TransactionHistoryView(
                                        transactions = allTransactions,
                                        categories = categories,
                                        onEditTransaction = { tx ->
                                            editingTransaction = tx
                                            showNewTransactionModal = true
                                        },
                                        onDeleteTransaction = { tx ->
                                            scope.launch {
                                                db.transactionDao().deleteTransaction(tx)
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(70.dp))
                                }
                            }

                            // Month Detail Modal
                            if (selectedMonthForDetail != null) {
                                MonthDetailModal(
                                    monthOption = selectedMonthForDetail!!,
                                    allTransactions = allTransactions,
                                    categories = categories,
                                    onDismiss = { selectedMonthForDetail = null },
                                    onEditTransaction = { tx ->
                                        editingTransaction = tx
                                        showNewTransactionModal = true
                                    },
                                    onDeleteTransaction = { tx ->
                                        scope.launch {
                                            db.transactionDao().deleteTransaction(tx)
                                        }
                                    }
                                )
                            }

                            // New / Edit Transaction Modal
                            if (showNewTransactionModal) {
                                NewTransactionModal(
                                    categories = categories,
                                    preselectedCategoryId = preselectedCategoryIdForNewTx,
                                    editingTransaction = editingTransaction,
                                    onAddTransaction = { newTx ->
                                        scope.launch {
                                            db.transactionDao().insertTransaction(newTx)
                                        }
                                    },
                                    onUpdateTransaction = { updatedTx ->
                                        scope.launch {
                                            db.transactionDao().updateTransaction(updatedTx)
                                        }
                                    },
                                    onDismiss = {
                                        showNewTransactionModal = false
                                        preselectedCategoryIdForNewTx = null
                                        editingTransaction = null
                                    }
                                )
                            }

                            // ── Update Dialog ────────────────────────────────────
                            pendingUpdate?.let { info ->
                                UpdateDialog(
                                    updateInfo = info,
                                    onDismiss = { pendingUpdate = null }
                                )
                            }
                            // ────────────────────────────────────────────────────

                            selectedCategoryForDetail?.let { cat ->
                                CategoryDetailModal(
                                    category = cat,
                                    transactions = allTransactions,
                                    onUpdateCategoryLimit = { newLimit ->
                                        scope.launch {
                                            val updated = cat.copy(limit = newLimit)
                                            db.categoryBudgetDao().updateCategory(updated)
                                            selectedCategoryForDetail = updated
                                        }
                                    },
                                    onAddExpense = {
                                        preselectedCategoryIdForNewTx = cat.id
                                        editingTransaction = null
                                        showNewTransactionModal = true
                                    },
                                    onEditTransaction = { tx ->
                                        editingTransaction = tx
                                        showNewTransactionModal = true
                                    },
                                    onDeleteTransaction = { tx ->
                                        scope.launch {
                                            db.transactionDao().deleteTransaction(tx)
                                        }
                                    },
                                    onDismiss = { selectedCategoryForDetail = null }
                                )
                            }

                            if (showUnassignedModal && unassignedTransactions.isNotEmpty()) {
                                UnassignedCategorizationModal(
                                    unassignedTransactions = unassignedTransactions,
                                    categories = categories,
                                    onAssignCategory = { txId, catId ->
                                        scope.launch {
                                            val tx = db.transactionDao().getTransactionById(txId)
                                            tx?.let {
                                                db.transactionDao().updateTransaction(it.copy(categoryId = catId))
                                            }
                                            if (unassignedTransactions.size <= 1) {
                                                showUnassignedModal = false
                                            }
                                        }
                                    },
                                    onDismiss = { showUnassignedModal = false }
                                )
                            }
                        }
                    }

                    if (showSettingsModal) {
                        Box(modifier = Modifier.fillMaxSize().padding(bottom = 0.dp)) {
                            SettingsScreen(
                                onClearAllData = {
                                    scope.launch {
                                        db.transactionDao().deleteAllTransactions()
                                        db.categoryBudgetDao().deleteAllCategories()
                                        prefManager.clearAll()
                                        isOnboardingCompleted = false
                                    }
                                },
                                categories = categories,
                                onAddCategory = { newCat ->
                                    scope.launch {
                                        db.categoryBudgetDao().insertCategory(newCat)
                                    }
                                },
                                onUpdateCategory = { updatedCat ->
                                    scope.launch {
                                        db.categoryBudgetDao().updateCategory(updatedCat)
                                    }
                                },
                                onDeleteCategory = { catToDelete ->
                                    scope.launch {
                                        db.categoryBudgetDao().deleteCategory(catToDelete)
                                    }
                                },
                                onDismiss = { showSettingsModal = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
