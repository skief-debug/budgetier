package com.budgettracker.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper class to seed default categories on initial launch if database is empty.
 * Synthetic fake transactions are disabled to ensure a clean start state.
 */
object MockDataGenerator {

    val defaultCategories = listOf(
        CategoryBudget(
            id = 1,
            title = "Essen & Trinken",
            limit = 450.0,
            type = "AUSGABE",
            position = 0,
            isExcluded = false,
            colorHex = "#00E676",
            iconName = "restaurant"
        ),
        CategoryBudget(
            id = 2,
            title = "Auto & Transport",
            limit = 250.0,
            type = "AUSGABE",
            position = 1,
            isExcluded = false,
            colorHex = "#2196F3",
            iconName = "directions_car"
        ),
        CategoryBudget(
            id = 3,
            title = "Freizeit & Hobby",
            limit = 200.0,
            type = "AUSGABE",
            position = 2,
            isExcluded = false,
            colorHex = "#AB47BC",
            iconName = "sports_esports"
        ),
        CategoryBudget(
            id = 4,
            title = "Miete & Wohnen",
            limit = 850.0,
            type = "AUSGABE",
            position = 3,
            isExcluded = false,
            colorHex = "#FF9800",
            iconName = "home"
        ),
        CategoryBudget(
            id = 5,
            title = "Spar-Puffer",
            limit = 300.0,
            type = "RUECKLAGE",
            position = 4,
            isExcluded = false,
            colorHex = "#26A69A",
            iconName = "savings"
        )
    )

    suspend fun seedDefaultCategoriesIfEmpty(database: AppDatabase) = withContext(Dispatchers.IO) {
        val categoryDao = database.categoryBudgetDao()
        val existingCategories = categoryDao.getAllCategoriesList()
        if (existingCategories.isEmpty()) {
            categoryDao.insertCategories(defaultCategories)
        }
    }
}
