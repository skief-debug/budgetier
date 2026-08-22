package com.budgettracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for CategoryBudget entities providing full CRUD operations.
 */
@Dao
interface CategoryBudgetDao {

    @Query("SELECT * FROM category_budgets ORDER BY position ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryBudget>>

    @Query("SELECT * FROM category_budgets ORDER BY position ASC")
    suspend fun getAllCategoriesList(): List<CategoryBudget>

    @Query("SELECT * FROM category_budgets WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryBudget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryBudget): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryBudget>)

    @Update
    suspend fun updateCategory(category: CategoryBudget)

    @Update
    suspend fun updateCategories(categories: List<CategoryBudget>)

    @Delete
    suspend fun deleteCategory(category: CategoryBudget)

    @Query("DELETE FROM category_budgets")
    suspend fun deleteAllCategories()
}
