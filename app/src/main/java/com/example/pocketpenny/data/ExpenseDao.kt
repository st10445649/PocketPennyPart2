package com.example.pocketpenny.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/*
Author: Android Developers
Date Accessed: 27 April 2026
Link: https://developer.android.com/training/data-storage/room/async-queries
Reason: Guidelines for accessing data from RoomDB using DAO queries
*/
@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM category_table WHERE userId = :userId")
    fun getAllCategories(userId: Int): Flow<List<Category>>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expense_table WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpenses(userId : Int): Flow<List<Expense>>

    @Query("SELECT * FROM category_table WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getCategoryById(id: Int, userId: Int): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Query("SELECT * FROM budget_table WHERE userId = :userId")
    fun getAllBudgets(userId : Int): Flow<List<Budget>>

    @Query("SELECT * FROM budget_table WHERE categoryId = :categoryId AND userId = :userId LIMIT 1")
    suspend fun getBudgetByCategoryId(categoryId: Int, userId: Int): Budget?

    @Query("SELECT id FROM budget_table WHERE categoryId = :catId AND monthYear = :month AND userId = :userId LIMIT 1")
    suspend fun getBudgetId(catId: Int, month: String, userId: Int): Int?

    @Query("SELECT * FROM budget_table WHERE monthYear = :monthYear AND userId = :userId")
    suspend fun getBudgetsForMonth(monthYear: String, userId: Int): List<Budget>

    @Query("SELECT * FROM expense_table WHERE userId = :userId AND  date >= :startDate AND date <= :endDate")
    fun getExpensesByDate( userId: Int,startDate: Long, endDate: Long): Flow<List<Expense>>
}